package com.store.domain.model.order.data;

import java.util.Date;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class OrderPaymentItemCreationData {
	@NonNull
	private Long orderId;
	@NonNull
	private Long userId;
	@NonNull
	private Double amount;
	@NonNull
	private Date date;
}
