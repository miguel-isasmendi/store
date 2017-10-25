package com.store.domain.model.order.build.coordinator;

import com.store.architecture.utils.DateUtils;
import com.store.domain.model.order.OrderDelivery;
import com.store.domain.model.order.data.OrderCreationDeliveryData;
import com.store.domain.model.order.data.OrderDeliveryData;
import com.store.domain.model.order.data.OrderDeliveryData.OrderDeliveryDataBuilder;
import com.store.domain.model.order.dto.OrderCreationDeliveryDto;
import com.store.domain.model.order.dto.OrderDeliveryDto;

public class OrderDeliveryBuildCoordinator {
	public static OrderCreationDeliveryData toData(OrderCreationDeliveryDto delivery) {

		return OrderCreationDeliveryData.builder().amount(delivery.getAmount()).dueDate(delivery.getDueDate()).build();
	}

	public static OrderDeliveryData toData(OrderDelivery delivery) {
		OrderDeliveryDataBuilder builder = OrderDeliveryData.builder();

		return builder.createdByUserId(delivery.getCreatedByUserId())
				.createdOn(DateUtils.dateFrom(delivery.getCreatedOn())).orderId(delivery.getOrderId())
				.orderDeliveryId(delivery.getOrderDeliveryId()).amount(delivery.getAmount())
				.dueDate(DateUtils.dateFrom(delivery.getDueDate())).status(delivery.getStatus()).build();
	}

	public static OrderDeliveryDto toDto(OrderDeliveryData delivery) {
		return OrderDeliveryDto.builder().amount(delivery.getAmount()).createdByUserId(delivery.getCreatedByUserId())
				.createdOn(delivery.getCreatedOn()).orderId(delivery.getOrderId())
				.deliveryId(delivery.getOrderDeliveryId()).dueDate(delivery.getDueDate()).status(delivery.getStatus())
				.build();
	}

}
