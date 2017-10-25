package com.store.domain.model.order.dto;

import java.util.Date;

import lombok.Getter;
import lombok.NonNull;

@Getter
public class OrderPaymentItemCreationDto {
	@NonNull
	private Double amount;
	@NonNull
	private Date date;
}
