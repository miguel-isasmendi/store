package com.store.domain.model.bundle.dto;

import java.util.Date;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;

@Getter
@Builder
public class BundleDto {
	@NonNull
	private Long bundleId;
	@NonNull
	private Long skuId;
	@NonNull
	private Date activeFrom;
	@NonNull
	private Date activeUntil;
	@Singular
	private List<BundleItemDto> items;
	@NonNull
	private Date createdOn;
	@NonNull
	private Long createdByUserId;
}
