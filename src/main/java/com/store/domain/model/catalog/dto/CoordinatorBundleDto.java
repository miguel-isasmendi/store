package com.store.domain.model.catalog.dto;

import java.util.Date;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;

@Getter
@Builder
public class CoordinatorBundleDto {
	@NonNull
	private Long bundleId;
	@NonNull
	private Long skuId;
	@NonNull
	private Long productId;
	@NonNull
	private String name;
	@NonNull
	private String description;
	@NonNull
	private Date activeFrom;
	private Date activeUntil;
	@Singular
	private List<CoordinatorBundleItemDto> items;
	@NonNull
	private Date createdOn;
	@NonNull
	private Long createdByUserId;
}
