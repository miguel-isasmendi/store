package com.store.domain.model.bundle.data;

import java.util.Date;
import java.util.List;

import com.store.domain.model.sku.SkuBillingType;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;

@Getter
@Builder
public class BundleCreationData {
	private Long productId;
	private Long skuId;
	private String name;
	private Double price;
	private String description;
	private SkuBillingType billingType;
	@NonNull
	private Date activeFrom;
	private Date activeUntil;
	@NonNull
	@Singular
	private List<BundleCreationItemData> items;
}
