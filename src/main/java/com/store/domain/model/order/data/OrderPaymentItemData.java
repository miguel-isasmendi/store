package com.store.domain.model.order.data;

import java.util.Date;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class OrderPaymentItemData {
	@NonNull
	private Long orderPaymentId;
	@NonNull
	private Long orderPaymentItemId;
	@NonNull
	private Long createdByUserId;
	@NonNull
	private Date createdOn;
	@NonNull
	private Date date;
	@NonNull
	private Double amount;
}
