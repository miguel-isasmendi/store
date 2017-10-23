package com.store.domain.model.product.data;

import java.util.List;

import com.store.domain.model.sku.data.SkuData;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;

@Getter
@Builder
public class FullProductData {
	@NonNull
	private Long productId;
	@NonNull
	private String name;
	@Singular("sku")
	private List<SkuData> skus;
}
