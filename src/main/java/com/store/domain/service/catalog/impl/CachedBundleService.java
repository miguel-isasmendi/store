package com.store.domain.service.catalog.impl;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.inject.Inject;
import com.store.architecture.cache.CacheHandler;
import com.store.domain.dao.catalog.BundleDao;
import com.store.domain.model.bundle.Bundle;
import com.store.domain.model.bundle.build.coordinator.BundleBuildCoordinator;
import com.store.domain.model.bundle.data.BundleCreationData;
import com.store.domain.model.bundle.data.BundleData;
import com.store.domain.service.catalog.BundleService;

import lombok.NonNull;

public class CachedBundleService implements BundleService {
	private static final String CACHE_BUNDLE_PREFIX = "bundle_";

	private BundleDao bundleDao;
	private CacheHandler<Bundle> bundleCacheHandler;

	@Inject
	public CachedBundleService(@NonNull BundleDao bundleDao, @NonNull MemcacheService cache) {
		this.bundleDao = bundleDao;

		this.bundleCacheHandler = CacheHandler.<Bundle>builder().cache(cache).keyGeneratorClosure(Bundle::getBundleId)
				.prefix(CACHE_BUNDLE_PREFIX).build();
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

}
