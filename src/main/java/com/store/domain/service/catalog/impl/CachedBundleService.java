package com.store.domain.service.catalog.impl;

import java.util.List;
import java.util.stream.Collectors;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.inject.Inject;
import com.store.architecture.cache.CacheHandler;
import com.store.domain.dao.catalog.BundleDao;
import com.store.domain.model.bundle.Bundle;
import com.store.domain.model.bundle.build.coordinator.BundleBuildCoordinator;
import com.store.domain.model.bundle.data.BundleCreationData;
import com.store.domain.model.bundle.data.BundleData;
import com.store.domain.model.product.data.ProductData;
import com.store.domain.service.catalog.BundleService;

import lombok.NonNull;

public class CachedBundleService implements BundleService {
	private static final String CACHE_BUNDLE_PREFIX = "bundle_";
	private static final String CACHE_BUNDLE_IDS_PREFIX = "products_ids";

	private BundleDao bundleDao;
	private CacheHandler<Bundle> bundleCacheHandler;
	private CacheHandler<List<Long>> bundleIdsCacheHandler;

	@Inject
	public CachedBundleService(@NonNull BundleDao bundleDao, @NonNull MemcacheService cache) {
		this.bundleDao = bundleDao;

		this.bundleCacheHandler = CacheHandler.<Bundle>builder().cache(cache).keyGeneratorClosure(Bundle::getBundleId)
				.prefix(CACHE_BUNDLE_PREFIX).build();
		
		this.bundleIdsCacheHandler = CacheHandler.<List<Long>>builder().cache(cache).prefix(CACHE_BUNDLE_IDS_PREFIX)
				.build();
	}

	@Override
	public BundleData create(@NonNull Long userId, @NonNull BundleCreationData bundleCreationData) {
		Bundle bundle = bundleDao.create(userId, bundleCreationData);

		bundleCacheHandler.putIntoCache(bundle);

		return BundleBuildCoordinator.toData(bundle);
	}

	@Override
	public BundleData getById(@NonNull Long bundleId) {
		Bundle bundle = bundleCacheHandler.getFromCacheUsingPartialKey(bundleId);

		if (bundle == null) {
			bundle = bundleDao.getById(bundleId);

			bundleCacheHandler.putIntoCache(bundle);
		}

		return BundleBuildCoordinator.toData(bundle);
	}

	@Override
	public BundleData deleteById(@NonNull Long bundleId) {
		bundleCacheHandler.deleteFromCacheUsingPartialKey(bundleId);
		return BundleBuildCoordinator.toData(bundleDao.delete(bundleId));
	}

	@Override
	public List<BundleData> getBundles() {
		List<Long> bundleIds = bundleIdsCacheHandler.getFromCache();

		if (bundleIds == null) {
			bundleIds = bundleDao.getBundlesIds();

			bundleIdsCacheHandler.putIntoCacheUsingPartialKey(bundleIds);
		}

		return bundleIds.stream().map(this::getById).collect(Collectors.toList());
	}
}
