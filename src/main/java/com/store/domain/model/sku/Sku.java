package com.store.domain.model.sku;

import java.io.Serializable;

import com.google.cloud.Timestamp;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class Sku implements Serializable {
	private static final long serialVersionUID = 1L;

	@NonNull
	private Long skuId;
	@NonNull
	private Long productId;
	@NonNull
	private String name;
	@NonNull
	private String currency;
	@NonNull
	private Double price;
	@NonNull
	private String description;
	@NonNull
	private Long skuTypeId;
	@NonNull
	private Boolean isDefault;
	@NonNull
	private Long createdByUserId;
	@NonNull
	private Timestamp createdOn;
	private Long madeInactiveBySkuId;
	private Timestamp validUntilDate;
}
