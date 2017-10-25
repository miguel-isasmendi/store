package com.store.domain.model.order.data;

import com.store.domain.model.order.OrderDeliveryStatus;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class OrderDeliveryStatusModificationData {
	@NonNull
	private OrderDeliveryStatus status;
}
