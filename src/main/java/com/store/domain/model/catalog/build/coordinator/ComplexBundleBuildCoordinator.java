package com.store.domain.model.catalog.build.coordinator;

import com.store.domain.model.bundle.data.BundleData;
import com.store.domain.model.bundle.data.BundleItemData;
import com.store.domain.model.catalog.data.CoordinatorBundleData;
import com.store.domain.model.catalog.data.CoordinatorBundleData.CoordinatorBundleDataBuilder;
import com.store.domain.model.catalog.dto.CoordinatorBundleDto;
import com.store.domain.model.catalog.dto.CoordinatorBundleDto.CoordinatorBundleDtoBuilder;
import com.store.domain.model.catalog.dto.CoordinatorBundleItemDto;
import com.store.domain.model.product.data.ProductData;
import com.store.domain.model.sku.data.SkuData;

import lombok.NonNull;

public class ComplexBundleBuildCoordinator {

	public static CoordinatorBundleData toData(BundleData bundle, ProductData product, SkuData sku) {
		CoordinatorBundleDataBuilder builder = CoordinatorBundleData.builder().activeFrom(bundle.getActiveFrom())
				.bundleId(bundle.getBundleId()).createdByUserId(bundle.getCreatedByUserId())
				.createdOn(bundle.getCreatedOn()).activeUntil(bundle.getActiveUntil()).items(bundle.getItems());

		builder.productId(product.getProductId());
		builder.name(product.getName());
		builder.description(product.getDescription());

		builder.skuId(sku.getSkuId());

		return builder.build();
	}

	public static CoordinatorBundleDto toDto(@NonNull CoordinatorBundleData bundle) {
		CoordinatorBundleDtoBuilder builder = CoordinatorBundleDto.builder().activeFrom(bundle.getActiveFrom())
				.activeUntil(bundle.getActiveUntil()).bundleId(bundle.getBundleId())
				.createdByUserId(bundle.getCreatedByUserId()).createdOn(bundle.getCreatedOn())
				.productId(bundle.getProductId()).name(bundle.getName()).description(bundle.getDescription())
				.skuId(bundle.getSkuId());

		for (BundleItemData bundleItem : bundle.getItems()) {
			builder.item(toDto(bundleItem));
		}

		return builder.build();
	}

	private static CoordinatorBundleItemDto toDto(@NonNull BundleItemData item) {
		return CoordinatorBundleItemDto.builder().bundleId(item.getBundleId()).bundleItemId(item.getBundleItemId())
				.createdByUserId(item.getCreatedByUserId()).createdOn(item.getCreatedOn()).quantity(item.getQuantity())
				.skuId(item.getSkuId()).build();
	}
}
