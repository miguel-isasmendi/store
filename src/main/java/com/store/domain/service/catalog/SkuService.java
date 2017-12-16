package com.store.domain.service.catalog;

import java.util.List;

import com.store.domain.model.sku.data.SkuCreationData;
import com.store.domain.model.sku.data.SkuData;

public interface SkuService {

	public SkuData create(Long userId, SkuCreationData skuData);

	public SkuData getById(Long skuId);

	public List<SkuData> getSkusByProductId(Long productId);

	public SkuData getByBundleId(Long bundleId);

}
