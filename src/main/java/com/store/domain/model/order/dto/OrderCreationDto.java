package com.store.domain.model.order.dto;

import java.util.List;

import lombok.Getter;
import lombok.NonNull;

@Getter
public class OrderCreationDto {
	@NonNull
	private OrderContactCreationDto contact;
	@NonNull
	private OrderCreationDeliveryDto delivery;
	@NonNull
	private List<OrderItemCreationDto> items;

	private List<OrderDiscountCreationDto> discounts;
}
