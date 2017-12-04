package com.store.domain.service.catalog.impl;

import java.util.List;
import java.util.stream.Collectors;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.inject.Inject;
import com.store.architecture.cache.CacheHandler;
import com.store.domain.dao.catalog.ProductDao;
import com.store.domain.model.product.Product;
import com.store.domain.model.product.build.coordinator.ProductBuildCoordinatorProvider;
import com.store.domain.model.product.data.ProductCreationData;
import com.store.domain.model.product.data.ProductData;
import com.store.domain.service.catalog.ProductService;

import lombok.NonNull;

public class CachedProductService implements ProductService {
	private static final String CACHE_PRODUCT_PREFIX = "product_";
	private static final String CACHE_PRODUCT_IDS_PREFIX = "products_ids";

	private ProductDao productDao;
	private CacheHandler<Product> productCacheHandler;
	private CacheHandler<List<Long>> productIdsCacheHandler;

	@Inject
	public CachedProductService(@NonNull ProductDao productDao, @NonNull MemcacheService cache) {
		this.productDao = productDao;

		this.productCacheHandler = CacheHandler.<Product>builder().cache(cache)
				.keyGeneratorClosure(Product::getProductId).prefix(CACHE_PRODUCT_PREFIX).build();

		this.productIdsCacheHandler = CacheHandler.<List<Long>>builder().cache(cache).prefix(CACHE_PRODUCT_IDS_PREFIX)
				.build();
	}

	@Override
	public List<ProductData> create(@NonNull Long userId, ProductCreationData... prodDataArray) {
		return productDao.create(userId, prodDataArray).stream().map(productCacheHandler::putIntoCache)
				.map(ProductBuildCoordinatorProvider::toData).collect(Collectors.toList());
	}

	@Override
	public ProductData getById(@NonNull Long productId) {
		Product product = productCacheHandler.getFromCacheUsingPartialKey(productId);

		if (product == null) {
			product = productDao.getById(productId);
			productCacheHandler.putIntoCache(product);
		}

		return ProductBuildCoordinatorProvider.toData(product);
	}

	@Override
	public List<ProductData> getProducts() {
		List<Long> productsIds = productIdsCacheHandler.getFromCache();

		if (productsIds == null) {
			productsIds = productDao.getProductsIds();

			productIdsCacheHandler.putIntoCacheUsingPartialKey(productsIds);
		}

		return productsIds.stream().map(this::getById).collect(Collectors.toList());
	}

}
