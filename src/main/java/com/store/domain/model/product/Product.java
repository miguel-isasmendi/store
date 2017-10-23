package com.store.domain.model.product;

import java.io.Serializable;

import com.google.cloud.Timestamp;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class Product implements Serializable {
	private static final long serialVersionUID = 1L;

	@NonNull
	private Long productId;
	@NonNull
	private String name;
	@NonNull
	private String description;
	@NonNull
	private Long createdByUserId;
	@NonNull
	private Timestamp createdOn;
}
