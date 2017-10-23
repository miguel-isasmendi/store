package com.store.domain.model.product.dto;

import java.util.List;

import com.store.domain.model.sku.dto.SkuCreationDto;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;

@Getter
@Builder
public class ProductCreationDto {
	@NonNull
	private String name;
	@NonNull
	@Singular("sku")
	private List<SkuCreationDto> skus;
}
