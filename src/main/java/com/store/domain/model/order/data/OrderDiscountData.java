package com.store.domain.model.order.data;

import java.util.Date;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class OrderDiscountData {
	@NonNull
	private Long orderId;
	@NonNull
	private Long orderDiscountId;
	@NonNull
	private Double amount;
	@NonNull
	private Long createdByUserId;
	@NonNull
	private Date createdOn;
}
