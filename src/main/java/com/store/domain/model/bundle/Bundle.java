package com.store.domain.model.bundle;

import java.io.Serializable;
import java.util.List;

import com.google.cloud.Timestamp;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;

@Getter
@Builder
public class Bundle implements Serializable {
	private static final long serialVersionUID = 1L;

	@NonNull
	private Long bundleId;
	@NonNull
	private Long skuId;
	@NonNull
	private Timestamp activeFrom;
	@NonNull
	private Timestamp activeUntil;
	@Singular
	private List<BundleItem> items;
	@NonNull
	private Timestamp createdOn;
	@NonNull
	private Long createdByUserId;
}
