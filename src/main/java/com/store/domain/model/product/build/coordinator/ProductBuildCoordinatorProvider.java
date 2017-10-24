package com.store.domain.model.product.build.coordinator;

import java.util.List;
import java.util.stream.Collectors;

import com.store.architecture.utils.DateUtils;
import com.store.architecture.validator.ObjectBuildConversionOverseer;
import com.store.domain.model.product.Product;
import com.store.domain.model.product.data.FullProductData;
import com.store.domain.model.product.data.ProductCreationData;
import com.store.domain.model.product.data.ProductData;
import com.store.domain.model.product.dto.FullProductDto;
import com.store.domain.model.product.dto.ProductCreationDto;
import com.store.domain.model.product.dto.ProductDto;
import com.store.domain.model.sku.build.coordinator.SkuBuildCoordinatorProvider;
import com.store.domain.model.sku.data.SkuData;

public class ProductBuildCoordinatorProvider {

	public static FullProductDto toFullDto(FullProductData product) {
		return FullProductDto.builder().productId(product.getProductId()).createdByUserId(product.getCreatedByUserId())
				.createdOn(product.getCreatedOn()).name(product.getName()).description(product.getDescription())
				.skus(product.getSkus().stream().map(SkuBuildCoordinatorProvider::toDto).collect(Collectors.toList()))
				.build();
	}

	public static ProductDto toDto(ProductData product) {
		return ProductDto.builder().productId(product.getProductId()).name(product.getName()).build();
	}

	public static ProductData toData(Product product) {
		return ProductData.builder().productId(product.getProductId()).name(product.getName())
				.createdByUserId(product.getCreatedByUserId()).createdOn(DateUtils.dateFrom(product.getCreatedOn()))
				.description(product.getDescription()).build();
	}

	public static FullProductData toFullData(ProductData product, List<SkuData> skuDatas) {
		return FullProductData.builder().productId(product.getProductId()).name(product.getName())
				.createdByUserId(product.getCreatedByUserId()).createdOn(product.getCreatedOn())
				.description(product.getDescription()).skus(skuDatas).build();
	}

	public static ProductCreationData toData(ProductCreationDto productCreation) {
		return ProductCreationData.builder().name(productCreation.getName())
				.description(productCreation.getDescription()).skus(productCreation.getSkus().stream()
						.map(SkuBuildCoordinatorProvider::toData).collect(Collectors.toList()))
				.build();
	}

	public static ProductCreationData buildToData(
			ObjectBuildConversionOverseer<ProductCreationDto, ProductCreationData> overseer) {
		return toData(overseer.getInputObject());
	}
}
