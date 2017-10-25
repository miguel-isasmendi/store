package com.store.domain.model.order.build.coordinator;

import java.util.ArrayList;

import org.apache.commons.collections.CollectionUtils;

import com.store.architecture.utils.DateUtils;
import com.store.domain.model.order.OrderPayment;
import com.store.domain.model.order.OrderPaymentItem;
import com.store.domain.model.order.data.OrderPaymentData;
import com.store.domain.model.order.data.OrderPaymentData.OrderPaymentDataBuilder;
import com.store.domain.model.order.data.OrderPaymentItemData;
import com.store.domain.model.order.dto.OrderPaymentDto;
import com.store.domain.model.order.dto.OrderPaymentDto.OrderPaymentDtoBuilder;
import com.store.domain.model.order.dto.OrderPaymentItemDto;

public class OrderPaymentBuildCoordinator {
	public static OrderPaymentData toData(OrderPayment orderPayment) {
		OrderPaymentDataBuilder builder = OrderPaymentData.builder();

		builder.totalAmount(orderPayment.getTotalAmount()).createdByUserId(orderPayment.getCreatedByUserId())
				.createdOn(DateUtils.dateFrom(orderPayment.getCreatedOn())).orderId(orderPayment.getOrderId())
				.orderPaymentId(orderPayment.getOrderPaymentId()).status(orderPayment.getStatus());

		if (CollectionUtils.isEmpty(orderPayment.getItems())) {
			builder.items(new ArrayList<OrderPaymentItemData>(0));
		}

		for (OrderPaymentItem orderPaymentItem : orderPayment.getItems()) {
			builder.item(OrderPaymentItemBuildCoordinator.toData(orderPaymentItem));
		}

		return builder.build();
	}

	public static OrderPaymentDto toDto(OrderPaymentData orderPayment) {
		OrderPaymentDtoBuilder builder = OrderPaymentDto.builder();

		builder.totalAmount(orderPayment.getTotalAmount()).createdByUserId(orderPayment.getCreatedByUserId())
				.createdOn(orderPayment.getCreatedOn()).orderId(orderPayment.getOrderId())
				.orderPaymentId(orderPayment.getOrderPaymentId()).status(orderPayment.getStatus());

		if (CollectionUtils.isEmpty(orderPayment.getItems())) {
			builder.items(new ArrayList<OrderPaymentItemDto>(0));
		}

		for (OrderPaymentItemData orderPaymentItem : orderPayment.getItems()) {
			builder.item(OrderPaymentItemBuildCoordinator.toDto(orderPaymentItem));
		}

		return builder.build();
	}

}
