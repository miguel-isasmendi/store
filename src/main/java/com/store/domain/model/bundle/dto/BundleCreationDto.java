package com.store.domain.model.bundle.dto;

import java.util.Date;
import java.util.List;

import com.store.domain.model.sku.SkuBillingType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Singular;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BundleCreationDto {
	private Long productId;
	private Long skuId;
	private String name;
	private Double price;
	private String description;
	private SkuBillingType billingType;
	@NonNull
	private Date activeFrom;
	private Date activeUntil;
	@Singular
	private List<BundleCreationItemDto> items;
}
