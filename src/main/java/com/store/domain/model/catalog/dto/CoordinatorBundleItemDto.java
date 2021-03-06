package com.store.domain.model.catalog.dto;

import java.util.Date;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class CoordinatorBundleItemDto {

	@NonNull
	private Long bundleId;
	@NonNull
	private Long bundleItemId;
	@NonNull
	private Long skuId;
	@NonNull
	private Long quantity;
	@NonNull
	private Date createdOn;
	@NonNull
	private Long createdByUserId;
}
