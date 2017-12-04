package com.store.domain.api.regular;

import static com.google.api.server.spi.config.ApiMethod.HttpMethod.DELETE;
import static com.google.api.server.spi.config.ApiMethod.HttpMethod.GET;
import static com.google.api.server.spi.config.ApiMethod.HttpMethod.POST;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;

import com.google.api.server.spi.ServiceException;
import com.google.api.server.spi.auth.common.User;
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
import com.store.domain.model.order.build.coordinator.OrderBuildCoordinator;
import com.store.domain.model.order.build.coordinator.OrderPaymentItemBuildCoordinator;
import com.store.domain.model.order.build.validator.OrderCreationValidatorProvider;
import com.store.domain.model.order.data.OrderCreationCoordinatorData;
import com.store.domain.model.order.data.OrderData;
import com.store.domain.model.order.data.OrderDeliveryStatusModificationData;
import com.store.domain.model.order.data.OrderStatusModificationData;
import com.store.domain.model.order.dto.OrderCreationDto;
import com.store.domain.model.order.dto.OrderDeliveryStatusModificationDto;
import com.store.domain.model.order.dto.OrderDto;
import com.store.domain.model.order.dto.OrderPaymentCreationListDto;
import com.store.domain.model.user.data.UserData;
import com.store.domain.service.checkout.CheckoutCoordinatorService;
import com.store.domain.service.user.UserService;

import lombok.NonNull;

@ExceptionMapping(from = RequestBusinessValidationException.class, to = BadRequestException.class)
@ExceptionMapping(from = NotFoundDaoException.class, to = BadRequestException.class)
@ExceptionMapping(from = ConflictDaoException.class, to = ConflictException.class)
public class CheckoutApi extends FirebaseRegularUserAuthenticationProtectedApi {
	private CheckoutCoordinatorService checkoutCoordinatorService;
	private UserService userService;

	@Inject
	public CheckoutApi(@NonNull CheckoutCoordinatorService checkoutCoordinatorService,
			@NonNull UserService userService) {
		this.checkoutCoordinatorService = checkoutCoordinatorService;
		this.userService = userService;
	}

	@ApiMethod(httpMethod = POST, path = "/store/orders")
	@ExceptionMapping(from = InvalidArgumentsDaoException.class, to = BadRequestException.class)
	@ExceptionMapping(from = InvalidArgumentsServiceException.class, to = BadRequestException.class)
	public OrderDto createOrder(User firebaseUser, OrderCreationDto orderCreationDto) throws ServiceException {
		OrderCreationCoordinatorData orderCreationData = ObjectBuildConversionOverseer
				.<OrderCreationDto, OrderCreationCoordinatorData>builder().inputObject(orderCreationDto)
				.preBuildValidator(OrderCreationValidatorProvider::validateDtoToDataTranslation)
				.buildClosure(overseer -> OrderBuildCoordinator.toData(overseer.getInputObject())).build().execute();

		OrderData order = checkoutCoordinatorService.createOrder(userService.getByFirebaseId(firebaseUser.getId()),
				orderCreationData);

		return OrderBuildCoordinator.toDto(order);
	}

	@ApiMethod(httpMethod = GET, path = "/store/orders/{order_id}")
	public OrderDto getOrder(User firebaseUser, @Named("order_id") Long orderId) throws ServiceException {
		OrderData order = checkoutCoordinatorService.getOrder(userService.getByFirebaseId(firebaseUser.getId()),
				orderId);

		return OrderBuildCoordinator.toDto(order);
	}

	@ApiMethod(httpMethod = GET, path = "/store/orders")
	public ResponseListContainer<OrderDto> getOrders(User firebaseUser) throws ServiceException {

		ResponseListContainerBuilder<OrderDto> response = ResponseListContainer.<OrderDto>builder();

		List<OrderData> orders = checkoutCoordinatorService
				.getOrders(userService.getByFirebaseId(firebaseUser.getId()));

		for (OrderData order : orders) {
			response.item(OrderBuildCoordinator.toDto(order));
		}

		return response.build();
	}

	@ApiMethod(httpMethod = POST, path = "/store/orders/{order_id}/status")
	@ExceptionMapping(from = InvalidArgumentsDaoException.class, to = BadRequestException.class)
	@ExceptionMapping(from = NotFoundDaoException.class, to = NotFoundException.class)
	public OrderDto changeOrderStatus(User user, @Named("order_id") Long orderId,
			OrderStatusModificationData orderStatusModificationData) throws ServiceException {
		OrderData order = checkoutCoordinatorService.changeOrderStatus(userService.getByFirebaseId(user.getId()),
				orderId, orderStatusModificationData);

		return OrderBuildCoordinator.toDto(order);
	}

	@ApiMethod(httpMethod = POST, path = "/store/orders/{order_id}/payment")
	@ExceptionMapping(from = InvalidArgumentsDaoException.class, to = NotFoundException.class)
	@ExceptionMapping(from = NotFoundDaoException.class, to = NotFoundException.class)
	@ExceptionMapping(from = InvalidArgumentsServiceException.class, to = BadRequestException.class)
	public OrderDto createOrderPaymentItems(User firebaseUser, @Named("order_id") Long orderId,
			OrderPaymentCreationListDto orderPaymentRegistrationListDto) throws ServiceException {

		UserData user = userService.getByFirebaseId(firebaseUser.getId());

		OrderData order = checkoutCoordinatorService.createOrderPaymentItems(user, orderId,
				orderPaymentRegistrationListDto
						.getItems().stream().map(paymentItemDto -> OrderPaymentItemBuildCoordinator
								.toData(paymentItemDto, orderId, user.getUserId(), paymentItemDto.getDate()))
						.collect(Collectors.toList()));

		return OrderBuildCoordinator.toDto(order);
	}

	@ApiMethod(httpMethod = POST, path = "/store/orders/{order_id}/delivery")
	@ExceptionMapping(from = InvalidArgumentsDaoException.class, to = BadRequestException.class)
	@ExceptionMapping(from = NotFoundDaoException.class, to = NotFoundException.class)
	public OrderDto updateOrderDeliveryStatus(User user, @Named("order_id") Long orderId,
			OrderDeliveryStatusModificationDto statusModificationDto) throws ServiceException {
		OrderData order = checkoutCoordinatorService.updateOrderDeliveryStatus(
				userService.getByFirebaseId(user.getId()), orderId,
				OrderDeliveryStatusModificationData.builder().status(statusModificationDto.getStatus()).build());

		return OrderBuildCoordinator.toDto(order);
	}

	@ApiMethod(httpMethod = DELETE, path = "/store/orders/{order_id}/payment")
	@ExceptionMapping(from = InvalidArgumentsServiceException.class, to = BadRequestException.class)
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

		OrderData order = checkoutCoordinatorService.invalidatePaymentItem(userService.getByFirebaseId(user.getId()),
				orderId, paymentItemIds);

		return OrderBuildCoordinator.toDto(order);
	}
}
