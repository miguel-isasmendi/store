package com.store.domain.model.sku.data;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Builder
public class SkuCreationData {

	private Long skuId;
	@Setter
	private Long productId;
	@NonNull
	private String name;
	@NonNull
	private Double price;
	@NonNull
	private String description;
}
