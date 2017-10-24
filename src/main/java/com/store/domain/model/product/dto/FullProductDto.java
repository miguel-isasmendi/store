package com.store.domain.model.product.dto;

import java.util.Date;
import java.util.List;

import com.store.domain.model.sku.dto.SkuDto;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;

@Getter
@Builder
public class FullProductDto {
	@NonNull
	private Long productId;
	@NonNull
	private String name;
	@NonNull
	private String description;
	@Singular("sku")
	private List<SkuDto> skus;
	@NonNull
	private Long createdByUserId;
	@NonNull
	private Date createdOn;
}
