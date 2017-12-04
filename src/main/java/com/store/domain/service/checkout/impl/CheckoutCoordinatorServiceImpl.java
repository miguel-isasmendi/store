package com.store.domain.service.checkout.impl;

import java.util.List;

import com.google.inject.Inject;
import com.store.architecture.exception.service.InvalidArgumentsServiceException;
import com.store.domain.exception.ErrorConstants;
import com.store.domain.model.order.build.coordinator.OrderBuildCoordinator;
import com.store.domain.model.order.data.OrderCreationCoordinatorData;
import com.store.domain.model.order.data.OrderCreationData;
import com.store.domain.model.order.data.OrderData;
import com.store.domain.model.order.data.OrderDeliveryStatusModificationData;
import com.store.domain.model.order.data.OrderPaymentItemCreationData;
import com.store.domain.model.order.data.OrderStatusModificationData;
import com.store.domain.model.user.data.UserData;
import com.store.domain.service.catalog.CatalogCoordinatorService;
import com.store.domain.service.checkout.CheckoutCoordinatorService;
import com.store.domain.service.order.OrderService;

import lombok.NonNull;

public class CheckoutCoordinatorServiceImpl implements CheckoutCoordinatorService {
	private OrderService orderService;
	private CatalogCoordinatorService catalogCoordinatorService;

	@Inject
	public CheckoutCoordinatorServiceImpl(@NonNull OrderService orderService,
			@NonNull CatalogCoordinatorService catalogCoordinatorService) {
		this.orderService = orderService;
		this.catalogCoordinatorService = catalogCoordinatorService;
	}

	@Override
	public OrderData createOrder(@NonNull UserData user, @NonNull OrderCreationCoordinatorData orderCreationData) {

		OrderCreationData orderData = OrderBuildCoordinator.toData(orderCreationData,
				catalogCoordinatorService.getSkuService());

		Double deliveryCost = orderData.getDelivery().getAmount();
		Double discountCost = 0d;
		Double orderSubtotal = 0d;

		orderSubtotal += orderData.getItems().stream().map(item -> item.getQuantity() * item.getPrice()).reduce(0d,
				Double::sum);

		discountCost += orderData.getDiscounts().stream().map(discount -> discount.getAmount()).reduce(0d, Double::sum);

		Double totalAmount = (deliveryCost + orderSubtotal) - discountCost;

		if (discountCost > 0 && totalAmount < 1) {
			throw new InvalidArgumentsServiceException(ErrorConstants.AMOUNT_OF_DISCOUNTS_EXCEEDS_ORDER_TOTAL);
		}

		return orderService.create(user.getUserId(), orderData);
	}

	@Override
	public OrderData getOrder(@NonNull UserData user, @NonNull Long orderId) {
		return orderService.get(orderId);
	}

	@Override
	public List<OrderData> getOrders(@NonNull UserData user) {
		return orderService.getOrders();
	}

	@Override
	public OrderData changeOrderStatus(@NonNull UserData user, @NonNull Long orderId,
			@NonNull OrderStatusModificationData orderStatusModificationData) {

		return orderService.updateOrderStatus(user.getUserId(), orderId, orderStatusModificationData);
	}

	@Override
	public OrderData createOrderPaymentItems(@NonNull UserData user, @NonNull Long orderId,
			@NonNull List<OrderPaymentItemCreationData> orderPaymentCreationItems) {
		return orderService.createOrderPaymentItems(orderPaymentCreationItems);
	}

	@Override
	public OrderData updateOrderDeliveryStatus(@NonNull UserData user, @NonNull Long orderId,
			@NonNull OrderDeliveryStatusModificationData statusModificationData) {
		OrderData orderData = this.orderService.updateOrderDeliveryStatus(user.getUserId(), orderId,
				OrderDeliveryStatusModificationData.builder().status(statusModificationData.getStatus()).build());

		return orderData;
	}

	@Override
	public OrderData invalidatePaymentItem(@NonNull UserData user, @NonNull Long orderId,
			@NonNull List<Long> paymentItemIds) {

		return orderService.invalidatePaymentItem(orderId, paymentItemIds);
	}
}
