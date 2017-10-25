package com.store.domain.model.order.data;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Builder
public class OrderItemCreationData {
	@NonNull
	private Long skuId;
	@NonNull
	private Long quantity;
	@Setter
	@NonNull
	private Double price;
}
