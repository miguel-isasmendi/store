package com.store.domain.service.catalog.impl;

import java.util.List;
import java.util.stream.Collectors;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.inject.Inject;
import com.store.architecture.cache.CacheHandler;
import com.store.domain.dao.catalog.SkuDao;
import com.store.domain.model.sku.Sku;
import com.store.domain.model.sku.build.coordinator.SkuBuildCoordinatorProvider;
import com.store.domain.model.sku.data.SkuCreationData;
import com.store.domain.model.sku.data.SkuData;
import com.store.domain.service.catalog.SkuService;

import lombok.NonNull;

public class CachedSkuService implements SkuService {
	private static final String CACHE_SKU_PREFIX = "sku_";
	private static final String CACHE_SKUS_BY_PRODUCT_ID_PREFIX = CACHE_SKU_PREFIX + "ids_by_product_id_";

	private SkuDao skuDao;
	private CacheHandler<Sku> skuCacheHandler;
	private CacheHandler<List<Long>> skuByProductIdCacheHandler;

	@Inject
	public CachedSkuService(@NonNull SkuDao skuDao, @NonNull MemcacheService cache) {
		this.skuDao = skuDao;

		this.skuCacheHandler = CacheHandler.<Sku>builder().cache(cache).keyGeneratorClosure(Sku::getSkuId)
				.prefix(CACHE_SKU_PREFIX).build();

		this.skuByProductIdCacheHandler = CacheHandler.<List<Long>>builder().cache(cache)
				.prefix(CACHE_SKUS_BY_PRODUCT_ID_PREFIX).build();
	}

	@Override
	public SkuData create(@NonNull Long userId, SkuCreationData skuData) {
		Sku sku = skuDao.create(skuData, userId);

		skuCacheHandler.putIntoCache(sku);

		if (skuData.getSkuId() != null) {
			skuByProductIdCacheHandler.deleteFromCacheUsingPartialKey(sku.getProductId());
		}

		return SkuBuildCoordinatorProvider.toData(sku);
	}

	@Override
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
		List<Long> skuIds = skuByProductIdCacheHandler.getFromCacheUsingPartialKey(productId);
		List<SkuData> skuDatas = null;

		if (skuIds == null) {
			List<Sku> skus = skuDao.getSkusByProductId(productId);

			skuDatas = skus.stream().map(skuCacheHandler::putIntoCache).map(SkuBuildCoordinatorProvider::toData)
					.collect(Collectors.toList());

			skuIds = skuDatas.stream().map(SkuData::getSkuId).collect(Collectors.toList());

			skuByProductIdCacheHandler.putIntoCacheUsingPartialKey(productId, skuIds);
		} else {
			skuDatas = skuIds.stream().map(this::getById).collect(Collectors.toList());
		}

		return skuDatas;
	}

}
