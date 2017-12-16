package com.store.domain.model.bundle.build.coordinator;

import com.google.cloud.Timestamp;
import com.store.architecture.utils.DateUtils;
import com.store.domain.model.bundle.Bundle;
import com.store.domain.model.bundle.BundleItem;
import com.store.domain.model.bundle.data.BundleCreationData;
import com.store.domain.model.bundle.data.BundleCreationData.BundleCreationDataBuilder;
import com.store.domain.model.bundle.data.BundleCreationItemData;
import com.store.domain.model.bundle.data.BundleData;
import com.store.domain.model.bundle.data.BundleData.BundleDataBuilder;
import com.store.domain.model.bundle.data.BundleItemData;
import com.store.domain.model.bundle.dto.BundleCreationDto;
import com.store.domain.model.bundle.dto.BundleCreationItemDto;

import lombok.NonNull;

public class BundleBuildCoordinator {

	public static BundleData toData(@NonNull Bundle bundle) {
		BundleDataBuilder builder = BundleData.builder().activeFrom(DateUtils.dateFrom(bundle.getActiveFrom()))
				.bundleId(bundle.getBundleId()).createdByUserId(bundle.getCreatedByUserId())
				.createdOn(DateUtils.dateFrom(bundle.getCreatedOn()));

		if (bundle.getActiveUntil() != null) {
			builder.activeUntil(DateUtils.dateFrom(bundle.getActiveUntil()));
		}

		for (BundleItem bundleItem : bundle.getItems()) {
			builder.item(toData(bundleItem));
		}

		return builder.build();
	}

	private static BundleItemData toData(@NonNull BundleItem item) {
		return BundleItemData.builder().bundleId(item.getBundleId()).bundleItemId(item.getBundleItemId())
				.createdByUserId(item.getCreatedByUserId()).createdOn(DateUtils.dateFrom(item.getCreatedOn()))
				.quantity(item.getQuantity()).skuId(item.getSkuId()).build();
	}

	public static BundleCreationData toData(@NonNull BundleCreationDto bundle) {
		BundleCreationDataBuilder builder = BundleCreationData.builder().activeUntil(bundle.getActiveUntil())
				.name(bundle.getName()).price(bundle.getPrice()).description(bundle.getDescription())
				.skuId(bundle.getSkuId()).productId(bundle.getProductId()).billingType(bundle.getBillingType());

		builder.activeFrom(
				bundle.getActiveFrom() == null ? DateUtils.dateFrom(Timestamp.now()) : bundle.getActiveFrom());

		for (BundleCreationItemDto bundleItem : bundle.getItems()) {
			builder.item(toData(bundleItem));
		}

		return builder.build();
	}

	private static BundleCreationItemData toData(@NonNull BundleCreationItemDto item) {
		return BundleCreationItemData.builder().quantity(item.getQuantity()).skuId(item.getSkuId()).build();
	}

}
