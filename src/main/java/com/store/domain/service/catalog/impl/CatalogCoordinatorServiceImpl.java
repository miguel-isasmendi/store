package com.store.domain.service.catalog.impl;

import java.util.List;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import com.store.architecture.exception.service.InvalidArgumentsServiceException;
import com.store.domain.model.bundle.data.BundleCreationData;
import com.store.domain.model.bundle.data.BundleCreationItemData;
import com.store.domain.model.bundle.data.BundleData;
import com.store.domain.model.product.build.coordinator.ProductBuildCoordinatorProvider;
import com.store.domain.model.product.data.FullProductData;
import com.store.domain.model.product.data.ProductCreationData;
import com.store.domain.model.product.data.ProductData;
import com.store.domain.model.sku.data.SkuCreationData;
import com.store.domain.model.sku.data.SkuData;
import com.store.domain.service.catalog.BundleService;
import com.store.domain.service.catalog.CatalogCoordinatorService;
import com.store.domain.service.catalog.ProductService;
import com.store.domain.service.catalog.SkuService;

import lombok.Getter;
import lombok.NonNull;

public class CatalogCoordinatorServiceImpl implements CatalogCoordinatorService {
	@Getter(onMethod_ = { @Override })
	private ProductService productService;
	@Getter(onMethod_ = { @Override })
	private SkuService skuService;
	@Getter(onMethod_ = { @Override })
	private BundleService bundleService;

	@Inject
	public CatalogCoordinatorServiceImpl(@NonNull ProductService productService, @NonNull SkuService skuService,
			@NonNull BundleService bundleService) {
		this.productService = productService;
		this.skuService = skuService;
		this.bundleService = bundleService;
	}

	@Override
	public List<FullProductData> createProduct(@NonNull Long userId, List<ProductCreationData> prodDataArray) {

		List<FullProductData> result = prodDataArray.stream().map(productCreationData -> {
			ProductData productData = productService.create(userId, productCreationData).iterator().next();

			List<SkuData> skuData = productCreationData.getSkus().stream().map((skuCreationData) -> {
				skuCreationData.setProductId(productData.getProductId());
				return skuService.create(userId, skuCreationData);
			}).collect(Collectors.toList());

			return ProductBuildCoordinatorProvider.toFullData(productData, skuData);
		}).collect(Collectors.toList());

		return result;
	}

	@Override
	public FullProductData getProductById(@NonNull Long productId) {
		return ProductBuildCoordinatorProvider.toFullData(productService.getById(productId),
				skuService.getSkusByProductId(productId));
	}

	@Override
	public List<FullProductData> getFullProducts() {
		return productService.getProducts().stream().map(productData -> ProductBuildCoordinatorProvider
				.toFullData(productData, skuService.getSkusByProductId(productData.getProductId())))
				.collect(Collectors.toList());
	}

	@Override
	public BundleData createBundle(@NonNull Long userId, @NonNull BundleCreationData bundleCreationData) {

		Long skuId = bundleCreationData.getSkuId();

		// Checking skus existance
		bundleCreationData.getItems().stream().map(BundleCreationItemData::getSkuId).map(skuService::getById);

		if (skuId != null) {
			productService.getById(bundleCreationData.getProductId());

			SkuData sku = skuService.getById(skuId);

			if (sku.getProductId().longValue() != bundleCreationData.getProductId().longValue()) {
				throw new InvalidArgumentsServiceException(
						String.format("The SKU doesn't belong to the product received (productId = %s",
								bundleCreationData.getProductId()));
			}

			skuId = sku.getSkuId();
		}

		BundleData bundle = bundleService.create(userId, bundleCreationData);

		if (skuId == null) {
			try {

				if (bundleCreationData.getProductId() == null) {
					throw new InvalidArgumentsServiceException(
							"It's required to indicate the product id for a sku creation");
				}

				skuId = skuService.create(userId,
						SkuCreationData.builder().billingType(bundleCreationData.getBillingType())
								.description(bundleCreationData.getDescription()).name(bundleCreationData.getName())
								.price(bundleCreationData.getPrice()).productId(bundleCreationData.getProductId())
								.bundleId(bundle.getBundleId()).build())
						.getSkuId();
			} catch (RuntimeException e) {
				bundleService.deleteById(bundle.getBundleId());
				throw e;
			}
		}

		return bundle;
	}

	@Override
	public BundleData getBundleById(Long userId, Long bundleId) {
		return bundleService.getById(bundleId);
	}

}
