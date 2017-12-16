package com.store.domain.service.catalog;

import java.util.List;

import com.store.domain.model.bundle.data.BundleCreationData;
import com.store.domain.model.bundle.data.BundleData;

public interface BundleService {

	public BundleData create(Long userId, BundleCreationData bundleCreationData);

	public BundleData getById(Long bundleId);

	public BundleData deleteById(Long bundleId);

	public List<BundleData> getBundles();

}
