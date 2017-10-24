package com.store.domain.model.product.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class ProductDto {
	@NonNull
	private Long productId;
	@NonNull
	private String name;
	@NonNull
	private String description;
}
