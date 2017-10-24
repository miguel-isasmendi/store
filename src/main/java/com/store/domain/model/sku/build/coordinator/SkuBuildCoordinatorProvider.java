package com.store.domain.model.sku.build.coordinator;

import com.store.domain.model.sku.Sku;
import com.store.domain.model.sku.data.SkuCreationData;
import com.store.domain.model.sku.data.SkuData;
import com.store.domain.model.sku.dto.SkuCreationDto;
import com.store.domain.model.sku.dto.SkuDto;

public class SkuBuildCoordinatorProvider {

	public static SkuCreationData toData(SkuCreationDto sku) {
		return SkuCreationData.builder().skuId(sku.getSkuId()).productId(sku.getProductId())
				.description(sku.getDescription()).name(sku.getName()).price(sku.getPrice()).build();
	}

	public static SkuData toData(Sku sku) {
		return SkuData.builder().skuId(sku.getSkuId()).productId(sku.getProductId()).description(sku.getDescription())
				.name(sku.getName()).price(sku.getPrice()).bundleId(sku.getBundleId()).build();
	}

	public static SkuDto toDto(SkuData sku) {
		return SkuDto.builder().skuId(sku.getSkuId()).productId(sku.getProductId()).description(sku.getDescription())
				.name(sku.getName()).price(sku.getPrice()).bundleId(sku.getBundleId()).build();
	}
}
