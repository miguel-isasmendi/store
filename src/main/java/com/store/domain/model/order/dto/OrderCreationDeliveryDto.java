package com.store.domain.model.order.dto;

import java.util.Date;

import lombok.Getter;
import lombok.NonNull;

@Getter
public class OrderCreationDeliveryDto {
	@NonNull
	private Double amount;
	@NonNull
	private Date dueDate;
}
