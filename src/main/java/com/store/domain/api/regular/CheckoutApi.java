package com.store.domain.api.regular;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;

import com.google.api.server.spi.ServiceException;
import com.google.api.server.spi.auth.common.User;
import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.Named;
import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.ConflictException;
import com.google.api.server.spi.response.NotFoundException;
import com.google.inject.Inject;
import com.store.architecture.exception.annotation.ExceptionMapping;
import com.store.architecture.exception.dao.ConflictDaoException;
import com.store.architecture.exception.dao.InvalidArgumentsDaoException;
import com.store.architecture.exception.dao.NotFoundDaoException;
import com.store.architecture.exception.service.InvalidArgumentsServiceException;
import com.store.architecture.exception.validation.RequestBusinessValidationException;
import com.store.architecture.model.RequestListContainer;
import com.store.architecture.model.ResponseListContainer;
import com.store.architecture.model.ResponseListContainer.ResponseListContainerBuilder;
import com.store.architecture.validator.ObjectBuildConversionOverseer;
import com.store.domain.architecture.api.regular.FirebaseRegularUserAuthenticationProtectedApi;
import com.store.domain.exception.ErrorConstants;
import com.store.domain.model.client.data.ClientData;
import com.store.domain.model.order.build.coordinator.OrderBuildCoordinator;
import com.store.domain.model.order.build.coordinator.OrderPaymentItemBuildCoordinator;
import com.store.domain.model.order.build.validator.OrderCreationValidatorProvider;
import com.store.domain.model.order.data.OrderContactCreationData;
import com.store.domain.model.order.data.OrderCreationData;
import com.store.domain.model.order.data.OrderData;
import com.store.domain.model.order.data.OrderDeliveryStatusModificationData;
import com.store.domain.model.order.data.OrderStatusModificationData;
import com.store.domain.model.order.dto.OrderContactCreationDto;
import com.store.domain.model.order.dto.OrderCreationDto;
import com.store.domain.model.order.dto.OrderDeliveryStatusModificationDto;
import com.store.domain.model.order.dto.OrderDto;
import com.store.domain.model.order.dto.OrderPaymentCreationListDto;
import com.store.domain.model.order.dto.OrderStatusModificationDto;
import com.store.domain.service.catalog.CatalogCoordinatorService;
import com.store.domain.service.client.ClientService;
import com.store.domain.service.order.OrderService;
import com.store.domain.service.user.UserService;

@ExceptionMapping(from = RequestBusinessValidationException.class, to = BadRequestException.class)
@ExceptionMapping(from = NotFoundDaoException.class, to = BadRequestException.class)
@ExceptionMapping(from = ConflictDaoException.class, to = ConflictException.class)
public class CheckoutApi extends FirebaseRegularUserAuthenticationProtectedApi {
	private OrderService orderService;
	private UserService userService;
	private ClientService clientService;
	private CatalogCoordinatorService catalogService;

	@Inject
	public CheckoutApi(OrderService orderService, UserService userService, ClientService clientService,
			CatalogCoordinatorService catalogService) {
		this.orderService = orderService;
		this.userService = userService;
		this.clientService = clientService;
		this.catalogService = catalogService;
	}

	@ApiMethod(httpMethod = ApiMethod.HttpMethod.POST, path = "/store/orders")
	@ExceptionMapping(from = InvalidArgumentsDaoException.class, to = BadRequestException.class)
	public OrderDto createOrder(User firebaseUser, OrderCreationDto orderRegistrationDto) throws ServiceException {

		Long userId = userService.getByFirebaseId(firebaseUser.getId()).getUserId();

		OrderCreationData orderData = ObjectBuildConversionOverseer.<OrderCreationDto, OrderCreationData>builder()
				.inputObject(orderRegistrationDto)
				.preBuildValidator(OrderCreationValidatorProvider::validateDtoToDataTranslation)
				.buildClosure(overseer -> OrderBuildCoordinator.toData(overseer.getInputObject(), catalogService))
				.postBuildValidator(overseer -> {
					Double deliveryCost = overseer.getBuiltObject().getDelivery().getAmount();
					Double discountCost = 0d;
					Double orderSubtotal = 0d;

					orderSubtotal += overseer.getBuiltObject().getItems().stream()
							.map(item -> item.getQuantity() * item.getPrice()).reduce(0d, Double::sum);

					discountCost += overseer.getBuiltObject().getDiscounts().stream()
							.map(discount -> discount.getAmount()).reduce(0d, Double::sum);

					Double totalAmount = (deliveryCost + orderSubtotal) - discountCost;

					if (discountCost > 0 && totalAmount < 1) {
						throw new RequestBusinessValidationException(
								ErrorConstants.AMOUNT_OF_DISCOUNTS_EXCEEDS_ORDER_TOTAL);
					}

				}).build().execute();

		dealWithClientData(orderRegistrationDto.getContact(), orderData.getContact());

		OrderData orderCreated = orderService.create(userId, orderData);

		return OrderBuildCoordinator.toDto(orderCreated, catalogService);

	}

	private void dealWithClientData(OrderContactCreationDto contactDto, OrderContactCreationData contactData) {
		ClientData client = clientService.getById(contactDto.getClientId());

		// I update the request data with the newly created client or with the data
		// received from the external service.
		contactData.setClientId(client.getClientId());
		contactData.setFirstName(client.getFirstName());
		contactData.setLastName(client.getLastName());
		contactData.setEmail(client.getEmail());
	}

	@ApiMethod(httpMethod = ApiMethod.HttpMethod.GET, path = "/store/orders/{order_id}")
	public OrderDto getOrder(User user, @Named("order_id") Long orderId) throws ServiceException {
		return OrderBuildCoordinator.toDto(orderService.get(orderId), catalogService);
	}

	@ApiMethod(httpMethod = ApiMethod.HttpMethod.GET, path = "/store/orders")
	public ResponseListContainer<OrderDto> getOrders(User user) throws ServiceException {

		ResponseListContainerBuilder<OrderDto> responseContainerBuilder = ResponseListContainer.<OrderDto>builder();
		responseContainerBuilder.items(orderService.getOrders().stream()
				.map(order -> OrderBuildCoordinator.toDto(order, catalogService)).collect(Collectors.toList()));

		return responseContainerBuilder.build();
	}

	@ApiMethod(httpMethod = ApiMethod.HttpMethod.POST, path = "/store/orders/{order_id}/status")
	@ExceptionMapping(from = InvalidArgumentsDaoException.class, to = BadRequestException.class)
	@ExceptionMapping(from = NotFoundDaoException.class, to = NotFoundException.class)
	public OrderDto changeOrderStatus(User user, @Named("order_id") Long orderId,
			OrderStatusModificationDto orderStatusModificationDto) throws ServiceException {
		OrderStatusModificationData orderStatusData = OrderBuildCoordinator.toData(orderStatusModificationDto);

		return OrderBuildCoordinator.toDto(orderService.updateOrderStatus(
				userService.getByFirebaseId(user.getId()).getUserId(), orderId, orderStatusData), catalogService);
	}

	@ApiMethod(httpMethod = ApiMethod.HttpMethod.POST, path = "/store/orders/{order_id}/payment")

	@ExceptionMapping(from = InvalidArgumentsDaoException.class, to = NotFoundException.class)
	@ExceptionMapping(from = NotFoundDaoException.class, to = NotFoundException.class)
	@ExceptionMapping(from = InvalidArgumentsServiceException.class, to = BadRequestException.class)
	public OrderDto createOrderPaymentItem(User user, @Named("order_id") Long orderId,
			OrderPaymentCreationListDto orderPaymentRegistrationListDto) throws ServiceException {

		Long userId = userService.getByFirebaseId(user.getId()).getUserId();

		OrderData order = orderService
				.createOrderPaymentItems(orderPaymentRegistrationListDto
						.getItems().stream().map(paymentItemDto -> OrderPaymentItemBuildCoordinator
								.toData(paymentItemDto, orderId, userId, paymentItemDto.getDate()))
						.collect(Collectors.toList()));

		return OrderBuildCoordinator.toDto(order, catalogService);
	}

	@ApiMethod(httpMethod = ApiMethod.HttpMethod.POST, path = "/store/orders/{order_id}/delivery")

	@ExceptionMapping(from = InvalidArgumentsDaoException.class, to = BadRequestException.class)
	@ExceptionMapping(from = NotFoundDaoException.class, to = NotFoundException.class)
	public OrderDto updateOrderDeliveryStatus(User user, @Named("order_id") Long orderId,
			OrderDeliveryStatusModificationDto statusModificationDto) throws ServiceException {
		OrderData orderData = this.orderService.updateOrderDeliveryStatus(
				userService.getByFirebaseId(user.getId()).getUserId(), orderId,
				OrderDeliveryStatusModificationData.builder().status(statusModificationDto.getStatus()).build());

		return OrderBuildCoordinator.toDto(orderData, catalogService);
	}

	@ApiMethod(httpMethod = ApiMethod.HttpMethod.DELETE, path = "/store/orders/{order_id}/payment")
	public OrderDto invalidatePaymentItem(User user, @Named("order_id") Long orderId,
			RequestListContainer<Object> paymentItemIdsContainer) throws ServiceException {

		if (paymentItemIdsContainer == null || CollectionUtils.isEmpty(paymentItemIdsContainer.getItems())) {
			throw new BadRequestException(
					ErrorConstants.formatError(ErrorConstants.ATTRIBUTE_SHOULD_NOT_BE_EMPTY, "payments"));
		}

		List<Long> paymentItemIds = paymentItemIdsContainer.getItems().stream().map(Object::toString)
				.map(Long::parseLong).collect(Collectors.toList());

		if (paymentItemIds.stream().collect(Collectors.toSet()).size() != paymentItemIds.size()) {
			throw new BadRequestException(
					ErrorConstants.formatError(ErrorConstants.SHOULD_NOT_HAVE_REPEATED_ELEMENTS, "payments"));
		}

		OrderData newOrderData = orderService.invalidatePaymentItem(orderId, paymentItemIds);

		return OrderBuildCoordinator.toDto(newOrderData, catalogService);
	}
}
