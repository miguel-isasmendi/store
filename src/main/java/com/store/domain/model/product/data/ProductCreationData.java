package com.store.domain.model.product.data;

import java.util.List;

import com.store.domain.model.sku.data.SkuCreationData;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;

@Getter
@Builder
public class ProductCreationData {
	@NonNull
	private String name;
	@Singular("sku")
	private List<SkuCreationData> skus;
}
