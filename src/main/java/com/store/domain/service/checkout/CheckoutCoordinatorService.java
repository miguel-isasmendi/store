package com.store.domain.service.checkout;

import java.util.List;

import com.store.domain.model.order.data.OrderCreationCoordinatorData;
import com.store.domain.model.order.data.OrderData;
import com.store.domain.model.order.data.OrderDeliveryStatusModificationData;
import com.store.domain.model.order.data.OrderPaymentItemCreationData;
import com.store.domain.model.order.data.OrderStatusModificationData;
import com.store.domain.model.user.data.UserData;

import lombok.NonNull;

public interface CheckoutCoordinatorService {
	public OrderData createOrder(UserData user, OrderCreationCoordinatorData orderCreationData);

	public OrderData getOrder(@NonNull UserData user, @NonNull Long orderId);

	public List<OrderData> getOrders(@NonNull UserData user);

	public OrderData changeOrderStatus(@NonNull UserData user, @NonNull Long orderId,
			@NonNull OrderStatusModificationData orderStatusModificationData);

	public OrderData createOrderPaymentItems(@NonNull UserData user, @NonNull Long orderId,
			@NonNull List<OrderPaymentItemCreationData> orderPaymentCreationItems);

	public OrderData updateOrderDeliveryStatus(@NonNull UserData user, @NonNull Long orderId,
			@NonNull OrderDeliveryStatusModificationData statusModificationData);

	public OrderData invalidatePaymentItem(UserData user, Long orderId, List<Long> paymentItemIds);
}
