package com.store.domain.model.product.data;

import java.util.Date;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class ProductData {

	@NonNull
	private Long productId;
	@NonNull
	private String name;
	@NonNull
	private String description;
	@NonNull
	private Long createdByUserId;
	@NonNull
	private Date createdOn;
}
