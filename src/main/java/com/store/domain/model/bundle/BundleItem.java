package com.store.domain.model.bundle;

import java.io.Serializable;

import com.google.cloud.Timestamp;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class BundleItem implements Serializable {
	private static final long serialVersionUID = 1L;

	@NonNull
	private Long bundleId;
	@NonNull
	private Long bundleItemId;
	@NonNull
	private Long skuId;
	@NonNull
	private Long quantity;
	@NonNull
	private Timestamp createdOn;
	@NonNull
	private Long createdByUserId;
}
