package com.store.domain.service.order.impl;

import java.util.List;
import java.util.stream.Collectors;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.inject.Inject;
import com.store.architecture.cache.CacheHandler;
import com.store.architecture.exception.service.InvalidArgumentsServiceException;
import com.store.domain.dao.order.OrderDao;
import com.store.domain.model.order.Order;
import com.store.domain.model.order.build.coordinator.OrderBuildCoordinator;
import com.store.domain.model.order.data.OrderCreationData;
import com.store.domain.model.order.data.OrderData;
import com.store.domain.model.order.data.OrderDeliveryStatusModificationData;
import com.store.domain.model.order.data.OrderPaymentItemCreationData;
import com.store.domain.model.order.data.OrderStatusModificationData;
import com.store.domain.service.order.OrderService;

import lombok.NonNull;

public class CachedOrderService implements OrderService {
	private static final String ORDER_CACHE_PREFIX = "order_";

	private OrderDao orderDao;
	private CacheHandler<Order> ordersCacheHandler;

	@Inject
	public CachedOrderService(OrderDao orderDao, MemcacheService cache) {
		super();
		this.orderDao = orderDao;
		ordersCacheHandler = CacheHandler.<Order>builder().cache(cache)
				.keyGeneratorClosure(element -> element.getOrderId()).prefix(ORDER_CACHE_PREFIX).build();
	}

	@Override
	public OrderData create(Long userId, OrderCreationData orderData) {
		Order createdOrder = orderDao.create(userId, orderData);

		ordersCacheHandler.putIntoCache(createdOrder);

		return OrderBuildCoordinator.toData(createdOrder);
	}

	@Override
	public OrderData get(@NonNull Long orderId) {
		Order order = ordersCacheHandler.getFromCacheUsingPartialKey(orderId);

		if (order == null) {
			order = orderDao.getById(orderId);

			ordersCacheHandler.putIntoCache(order);
		}

		return OrderBuildCoordinator.toData(order);
	}

	@Override
	public List<OrderData> getOrders() {
		return orderDao.getOrders().stream().map(OrderBuildCoordinator::toData).collect(Collectors.toList());
	}

	@Override
	public OrderData updateOrderDeliveryStatus(Long userId, Long orderId,
			OrderDeliveryStatusModificationData deliveryStatusData) {
		ordersCacheHandler.deleteFromCacheUsingPartialKey(orderId);

		Order order = orderDao.updateOrderDeliveryStatus(userId, orderId, deliveryStatusData);

		ordersCacheHandler.putIntoCache(order);

		return OrderBuildCoordinator.toData(order);
	}

	@Override
	public OrderData createOrderPaymentItems(@NonNull List<OrderPaymentItemCreationData> data) {
		Long orderId = data.stream().findFirst().orElseThrow(() -> new InvalidArgumentsServiceException(
				"This operation requires at least one order payment item to be created")).getOrderId();

		ordersCacheHandler.deleteFromCacheUsingPartialKey(orderId);

		Order order = this.orderDao.createPaymentItem(data);

		ordersCacheHandler.putIntoCache(order);

		return OrderBuildCoordinator.toData(order);

	}

	@Override
	public OrderData updateOrderStatus(Long userId, Long orderId, OrderStatusModificationData orderStatusData) {
		Order order = orderDao.updateOrderStatus(userId, orderId, orderStatusData);

		ordersCacheHandler.putIntoCache(order);

		return OrderBuildCoordinator.toData(order);
	}

	@Override
	public OrderData invalidatePaymentItem(Long orderId, List<Long> orderPaymentIdsToInvalidate) {
		Order order = orderDao.invalidatePaymentItem(orderId, orderPaymentIdsToInvalidate);

		ordersCacheHandler.putIntoCache(order);

		return OrderBuildCoordinator.toData(order);
	}

}
