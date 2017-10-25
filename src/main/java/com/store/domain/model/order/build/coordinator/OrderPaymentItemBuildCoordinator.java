package com.store.domain.model.order.build.coordinator;

import java.util.Date;

import com.store.architecture.utils.DateUtils;
import com.store.domain.model.order.OrderPaymentItem;
import com.store.domain.model.order.data.OrderPaymentItemCreationData;
import com.store.domain.model.order.data.OrderPaymentItemData;
import com.store.domain.model.order.dto.OrderPaymentItemCreationDto;
import com.store.domain.model.order.dto.OrderPaymentItemDto;

public class OrderPaymentItemBuildCoordinator {

	public static OrderPaymentItemCreationData toData(OrderPaymentItemCreationDto orderPayment, Long orderId,
			Long userId, Date paymentDate) {
		return OrderPaymentItemCreationData.builder().amount(orderPayment.getAmount()).userId(userId).orderId(orderId)
				.date(paymentDate).build();
	}

	public static OrderPaymentItemData toData(OrderPaymentItem orderPaymentItem) {

		return OrderPaymentItemData.builder().createdByUserId(orderPaymentItem.getCreatedByUserId())
				.createdOn(DateUtils.dateFrom(orderPaymentItem.getCreatedOn())).amount(orderPaymentItem.getAmount())
				.orderPaymentId(orderPaymentItem.getOrderPaymentId())
				.orderPaymentItemId(orderPaymentItem.getOrderPaymentItemId())
				.date(DateUtils.dateFrom(orderPaymentItem.getDate())).build();
	}

	public static OrderPaymentItemDto toDto(OrderPaymentItemData orderPaymentItem) {

		return OrderPaymentItemDto.builder().createdByUserId(orderPaymentItem.getCreatedByUserId())
				.createdOn(orderPaymentItem.getCreatedOn()).date(orderPaymentItem.getDate())
				.amount(orderPaymentItem.getAmount()).orderPaymentId(orderPaymentItem.getOrderPaymentId())
				.orderPaymentItemId(orderPaymentItem.getOrderPaymentItemId()).build();
	}
}
