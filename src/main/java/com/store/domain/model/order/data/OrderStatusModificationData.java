package com.store.domain.model.order.data;

import com.store.domain.model.order.OrderStatus;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class OrderStatusModificationData {
	@NonNull
	private OrderStatus status;
}
