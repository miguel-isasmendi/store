package com.store.domain.dao.catalog;

import com.store.domain.model.bundle.Bundle;
import com.store.domain.model.bundle.data.BundleCreationData;

public interface BundleDao {

	public Bundle create(Long userId, BundleCreationData bundleCreationData);

	public Bundle getById(Long bundleId);

	public Bundle delete(Long bundleId);
}
