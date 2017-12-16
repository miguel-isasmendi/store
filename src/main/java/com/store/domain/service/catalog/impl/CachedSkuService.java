package com.store.domain.service.catalog.impl;

import java.util.List;
import java.util.stream.Collectors;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.inject.Inject;
import com.store.architecture.cache.CacheHandler;
import com.store.architecture.exception.annotation.ExceptionMapping;
import com.store.architecture.exception.annotation.RequiresExceptionMappings;
import com.store.architecture.exception.dao.NotFoundDaoException;
import com.store.architecture.exception.service.InvalidArgumentsServiceException;
import com.store.domain.dao.catalog.SkuDao;
import com.store.domain.model.sku.Sku;
import com.store.domain.model.sku.build.coordinator.SkuBuildCoordinatorProvider;
import com.store.domain.model.sku.data.SkuCreationData;
import com.store.domain.model.sku.data.SkuData;
import com.store.domain.service.catalog.SkuService;

import lombok.NonNull;

@RequiresExceptionMappings
public class CachedSkuService implements SkuService {
	private static final String CACHE_SKU_PREFIX = "sku_";
	private static final String CACHE_SKU_BUNDLE_ID_PREFIX = CACHE_SKU_PREFIX + "bundle_id_";
	private static final String CACHE_SKUS_BY_PRODUCT_ID_PREFIX = CACHE_SKU_PREFIX + "ids_by_product_id_";

	private SkuDao skuDao;
	private CacheHandler<Sku> skuCacheHandler;
	private CacheHandler<Sku> skuByBundleIdCacheHandler;
	private CacheHandler<List<Long>> skuByProductIdCacheHandler;

	@Inject
	public CachedSkuService(@NonNull SkuDao skuDao, @NonNull MemcacheService cache) {
		this.skuDao = skuDao;

		this.skuCacheHandler = CacheHandler.<Sku>builder().cache(cache).keyGeneratorClosure(Sku::getSkuId)
				.prefix(CACHE_SKU_PREFIX).build();

		this.skuByBundleIdCacheHandler = CacheHandler.<Sku>builder().cache(cache).keyGeneratorClosure(Sku::getBundleId)
				.prefix(CACHE_SKU_BUNDLE_ID_PREFIX).build();

		this.skuByProductIdCacheHandler = CacheHandler.<List<Long>>builder().cache(cache)
				.prefix(CACHE_SKUS_BY_PRODUCT_ID_PREFIX).build();
	}

	@Override
	public SkuData create(@NonNull Long userId, @NonNull SkuCreationData skuData) {
		Sku sku = skuDao.create(skuData, userId);

		skuCacheHandler.putIntoCache(sku);

		if (skuData.getSkuId() != null) {
			skuByProductIdCacheHandler.deleteFromCacheUsingPartialKey(sku.getProductId());
		}

		return SkuBuildCoordinatorProvider.toData(sku);
	}

	@Override
	@ExceptionMapping(from = NotFoundDaoException.class, to = InvalidArgumentsServiceException.class)
	public SkuData getById(@NonNull Long skuId) {
		Sku sku = skuCacheHandler.getFromCacheUsingPartialKey(skuId);

		if (sku == null) {
			sku = skuDao.getById(skuId);

			skuCacheHandler.putIntoCache(sku);
		}

		return SkuBuildCoordinatorProvider.toData(sku);
	}

	@Override
	public List<SkuData> getSkusByProductId(@NonNull Long productId) {
		List<Long> skusIds = skuByProductIdCacheHandler.getFromCacheUsingPartialKey(productId);

		if (skusIds == null) {
			skusIds = skuDao.getSkusIdsByProductId(productId);

			skuByProductIdCacheHandler.putIntoCacheUsingPartialKey(productId, skusIds);
		}

		return skusIds.stream().map(this::getById).collect(Collectors.toList());
	}

	@Override
	@ExceptionMapping(from = NotFoundDaoException.class, to = InvalidArgumentsServiceException.class)
	public SkuData getByBundleId(@NonNull Long bundleId) {
		Sku sku = skuByBundleIdCacheHandler.getFromCacheUsingPartialKey(bundleId);

		if (sku == null) {
			sku = skuDao.getByBundleId(bundleId);

			skuCacheHandler.putIntoCache(sku);
		}

		return SkuBuildCoordinatorProvider.toData(sku);
	}
}
