package com.store.domain.model.order.dto;

import com.store.domain.model.order.OrderDeliveryStatus;

import lombok.Getter;
import lombok.NonNull;

@Getter
public class OrderDeliveryStatusModificationDto {
	@NonNull
	private OrderDeliveryStatus status;
}
