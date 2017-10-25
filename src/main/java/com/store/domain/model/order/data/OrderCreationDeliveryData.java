package com.store.domain.model.order.data;

import java.util.Date;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class OrderCreationDeliveryData {
	@NonNull
	private Double amount;
	@NonNull
	private Date dueDate;
}
