package com.store.domain.model.order.build.coordinator;

import com.store.architecture.utils.DateUtils;
import com.store.domain.model.order.OrderDiscount;
import com.store.domain.model.order.data.OrderDiscountData;
import com.store.domain.model.order.dto.OrderDiscountDto;

public class OrderDiscountBuildCoordinator {
	public static OrderDiscountData toData(OrderDiscount discount) {
		return OrderDiscountData.builder().orderId(discount.getOrderId()).orderDiscountId(discount.getOrderDiscountId())
				.amount(discount.getAmount()).createdByUserId(discount.getCreatedByUserId())
				.createdOn(DateUtils.dateFrom(discount.getCreatedOn())).build();
	}

	public static OrderDiscountDto toDto(OrderDiscountData discount) {
		return OrderDiscountDto.builder().orderId(discount.getOrderId()).orderDiscountId(discount.getOrderDiscountId())
				.amount(discount.getAmount()).createdByUserId(discount.getCreatedByUserId())
				.createdOn(discount.getCreatedOn()).build();
	}

	public static OrderDiscountData toData(OrderDiscountDto discount) {
		return OrderDiscountData.builder().orderId(discount.getOrderId()).orderDiscountId(discount.getOrderDiscountId())
				.amount(discount.getAmount()).createdByUserId(discount.getCreatedByUserId())
				.createdOn(discount.getCreatedOn()).build();
	}
}
