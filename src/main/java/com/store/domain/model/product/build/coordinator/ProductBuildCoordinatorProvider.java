package com.store.domain.model.product.build.coordinator;

import java.util.List;

import com.store.architecture.utils.DateUtils;
import com.store.architecture.validator.ObjectBuildConversionOverseer;
import com.store.domain.model.product.Product;
import com.store.domain.model.product.data.FullProductData;
import com.store.domain.model.product.data.ProductCreationData;
import com.store.domain.model.product.data.ProductData;
import com.store.domain.model.product.dto.FullProductDto;
import com.store.domain.model.product.dto.ProductCreationDto;
import com.store.domain.model.sku.data.SkuData;

public class ProductBuildCoordinatorProvider {

	public static FullProductDto toDto(FullProductData product) {
		return FullProductDto.builder().productId(product.getProductId()).name(product.getName()).build();
	}

	public static ProductData toData(Product product) {
		return ProductData.builder().productId(product.getProductId()).name(product.getName())
				.createdByUserId(product.getCreatedByUserId()).createdOn(DateUtils.dateFrom(product.getCreatedOn()))
				.description(product.getDescription()).build();
	}

	public static FullProductData toFullData(ProductData product, List<SkuData> skuDatas) {
		return FullProductData.builder().productId(product.getProductId()).name(product.getName()).skus(skuDatas)
				.build();
	}

	public static ProductCreationData toData(ProductCreationDto productCreation) {
		return ProductCreationData.builder().name(productCreation.getName()).build();
	}

	public static ProductCreationData buildToData(
			ObjectBuildConversionOverseer<ProductCreationDto, ProductCreationData> overseer) {
		return toData(overseer.getInputObject());
	}
}
