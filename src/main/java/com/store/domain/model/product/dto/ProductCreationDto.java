package com.store.domain.model.product.dto;

import java.util.List;

import com.store.domain.model.sku.dto.SkuCreationDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Singular;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreationDto {
	@NonNull
	private String name;
	@NonNull
	private String description;
	@NonNull
	@Singular("sku")
	private List<SkuCreationDto> skus;
}
