package com.store.domain.model.sku.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class SkuDto {

	@NonNull
	private Long skuId;
	@NonNull
	private Long productId;
	@NonNull
	private String name;
	@NonNull
	private Double price;
	@NonNull
	private String currency;
	@NonNull
	private String description;
}
