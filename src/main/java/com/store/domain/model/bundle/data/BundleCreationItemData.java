package com.store.domain.model.bundle.data;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class BundleCreationItemData {
	@NonNull
	private Long skuId;
	@NonNull
	private Long quantity;
}
