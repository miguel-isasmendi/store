package com.store.domain.model.order.dto;

import lombok.Getter;
import lombok.NonNull;

@Getter
public class OrderItemCreationDto {
	@NonNull
	private Long skuId;
	@NonNull
	private Long quantity;
}
