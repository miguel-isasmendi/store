package com.store.domain.model.sku.dto;

import com.store.domain.model.sku.SkuBillingType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkuCreationDto {
	private Long skuId;
	@Setter
	private Long productId;
	@NonNull
	private String name;
	@NonNull
	private Double price;
	@NonNull
	private String description;
	@NonNull
	private SkuBillingType billingType;
}
