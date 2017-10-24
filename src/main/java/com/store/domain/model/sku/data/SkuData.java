package com.store.domain.model.sku.data;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class SkuData {

	@NonNull
	private Long skuId;
	@NonNull
	private Long productId;
	private Long bundleId;
	@NonNull
	private String name;
	@NonNull
	private Double price;
	@NonNull
	private String description;
}