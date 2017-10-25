package com.store.domain.model.order.dto;

import com.store.domain.model.order.OrderStatus;

import lombok.Getter;
import lombok.NonNull;

@Getter
public class OrderStatusModificationDto {
	@NonNull
	private OrderStatus status;
}
