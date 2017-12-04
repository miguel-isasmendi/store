package com.store.domain.dao.order;

import java.util.List;

import com.store.domain.model.order.Order;
import com.store.domain.model.order.data.OrderCreationData;
import com.store.domain.model.order.data.OrderDeliveryStatusModificationData;
import com.store.domain.model.order.data.OrderPaymentItemCreationData;
import com.store.domain.model.order.data.OrderStatusModificationData;

import lombok.NonNull;

public interface OrderDao {

	public Order create(Long userId, OrderCreationData orderData);

	public Order getById(Long orderId);

	public Order updateOrderDeliveryStatus(Long userId, Long orderId,
			OrderDeliveryStatusModificationData deliveryStatusData);

	public Order createPaymentItem(List<OrderPaymentItemCreationData> orderPaymentData);

	public Order updateOrderStatus(Long userId, Long orderId, OrderStatusModificationData orderStatusData);

	public Order invalidatePaymentItem(@NonNull Long orderId, @NonNull List<Long> orderPaymentIdsToInvalidate);

	public List<Long> getOrdersIds();

}
