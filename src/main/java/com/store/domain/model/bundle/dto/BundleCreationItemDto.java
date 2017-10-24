package com.store.domain.model.bundle.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BundleCreationItemDto {
	@NonNull
	private Long skuId;
	@NonNull
	private Long quantity;
}
