package com.store.domain.model.order.dto;

import java.util.Date;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class OrderPaymentItemDto {
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
