package com.store.domain.model.order;

import java.io.Serializable;

import com.google.cloud.Timestamp;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class OrderDiscount implements Serializable {
	private static final long serialVersionUID = 1L;

	@NonNull
	private Long orderId;
	@NonNull
	private Long orderDiscountId;
	@NonNull
	private Long amount;
	@NonNull
	private Long createdByUserId;
	@NonNull
	private Timestamp createdOn;
}
