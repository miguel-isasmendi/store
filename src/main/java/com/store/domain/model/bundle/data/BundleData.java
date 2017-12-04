package com.store.domain.model.bundle.data;

import java.util.Date;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;

@Getter
@Builder
public class BundleData {
	@NonNull
	private Long bundleId;
	@NonNull
	private Date activeFrom;
	private Date activeUntil;
	@Singular
	private List<BundleItemData> items;
	@NonNull
	private Date createdOn;
	@NonNull
	private Long createdByUserId;
}
