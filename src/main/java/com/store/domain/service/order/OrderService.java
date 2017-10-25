package com.store.domain.service.order;

import java.util.List;

import com.store.domain.model.order.data.OrderCreationData;
import com.store.domain.model.order.data.OrderData;
import com.store.domain.model.order.data.OrderDeliveryStatusModificationData;
import com.store.domain.model.order.data.OrderPaymentItemCreationData;
import com.store.domain.model.order.data.OrderStatusModificationData;

import lombok.NonNull;

public interface OrderService {

	public OrderData create(Long userId, OrderCreationData orderData);

	public OrderData get(Long orderId);

	public OrderData updateOrderDeliveryStatus(Long userId, Long orderId,
			OrderDeliveryStatusModificationData deliveryStatusData);

	public List<OrderData> getOrders();

	public OrderData createOrderPaymentItems(List<OrderPaymentItemCreationData> data);

	public OrderData updateOrderStatus(Long userId, Long orderId, OrderStatusModificationData orderStatusData);

	public OrderData invalidatePaymentItem(@NonNull Long orderId, @NonNull List<Long> orderPaymentIdsToInvalidate);

}
