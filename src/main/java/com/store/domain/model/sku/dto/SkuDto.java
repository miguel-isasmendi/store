package com.store.domain.model.sku.dto;

import com.store.domain.model.sku.SkuBillingType;

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
	private Long bundleId;
	@NonNull
	private String name;
	@NonNull
	private Double price;
	@NonNull
	private String description;
	@NonNull
	private SkuBillingType billingType;
}
