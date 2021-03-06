package com.store.domain.dao.catalog;

import java.util.List;

import com.store.domain.model.sku.Sku;
import com.store.domain.model.sku.data.SkuCreationData;

public interface SkuDao {

	public List<Long> getSkusIdsByProductId(Long productId);

	public Sku create(SkuCreationData skudData, Long userId);

	public Sku getById(Long skuId);

	public Sku getByBundleId(Long bundleId);
}
