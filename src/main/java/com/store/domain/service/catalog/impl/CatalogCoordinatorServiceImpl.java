package com.store.domain.service.catalog.impl;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.store.domain.model.product.build.coordinator.ProductBuildCoordinatorProvider;
import com.store.domain.model.product.data.FullProductData;
import com.store.domain.model.product.data.ProductCreationData;
import com.store.domain.model.product.data.ProductData;
import com.store.domain.model.sku.data.SkuCreationData;
import com.store.domain.model.sku.data.SkuData;
import com.store.domain.service.catalog.CatalogCoordinatorService;
import com.store.domain.service.catalog.ProductService;
import com.store.domain.service.catalog.SkuService;

import lombok.NonNull;

public class CatalogCoordinatorServiceImpl implements CatalogCoordinatorService {

	private ProductService productService;
	private SkuService skuService;

	public CatalogCoordinatorServiceImpl(@NonNull ProductService productService, @NonNull SkuService skuService) {
		this.productService = productService;
		this.skuService = skuService;
	}

	@Override
	public List<FullProductData> createProduct(Long userId, ProductCreationData... prodDataArray) {

		List<FullProductData> result = Arrays.asList(prodDataArray).stream().map(productCreationData -> {
			ProductData productData = productService.create(userId, productCreationData).iterator().next();

			List<SkuData> skuData = productCreationData.getSkus().stream()
					.map(skuCreationData -> skuService.create(userId, skuCreationData)).collect(Collectors.toList());

			return ProductBuildCoordinatorProvider.toFullData(productData, skuData);
		}).collect(Collectors.toList());

		return result;
	}

	@Override
	public FullProductData getProductById(Long productId) {
		return ProductBuildCoordinatorProvider.toFullData(productService.getById(productId),
				skuService.getSkusByProductId(productId));
	}

	@Override
	public List<FullProductData> getFullProducts() {
		return productService.getProducts().stream().map(productData -> ProductBuildCoordinatorProvider.toFullData(productData,
				skuService.getSkusByProductId(productData.getProductId()))).collect(Collectors.toList());
	}

	@Override
	public SkuData createSku(Long userId, SkuCreationData skuData) {
		return skuService.create(userId, skuData);
	}

	@Override
	public SkuData getSkuById(Long skuId) {
		return skuService.getById(skuId);
	}

	@Override
	public List<ProductData> getProducts() {
		return productService.getProducts();
	}

}
