package com.store.domain.model.bundle.dto;

import java.util.Date;
import java.util.List;

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
	@NonNull
	private Long skuId;
	@NonNull
	private Date activeFrom;
	@NonNull
	private Date activeUntil;
	@Singular
	private List<BundleCreationItemDto> items;
}
