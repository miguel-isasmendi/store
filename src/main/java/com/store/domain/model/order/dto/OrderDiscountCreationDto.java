package com.store.domain.model.order.dto;

import lombok.Getter;
import lombok.NonNull;

@Getter
public class OrderDiscountCreationDto {
	@NonNull
	private Double amount;
}
