package com.store.domain.service.catalog;

import java.util.List;

import com.store.domain.model.bundle.data.BundleCreationData;
import com.store.domain.model.catalog.data.CoordinatorBundleData;
import com.store.domain.model.product.data.FullProductData;
import com.store.domain.model.product.data.ProductCreationData;

public interface CatalogCoordinatorService {

	public List<FullProductData> createProduct(Long userId, List<ProductCreationData> prodDataArray);

	public FullProductData getProductById(Long productId);

	public List<FullProductData> getFullProducts();

	public ProductService getProductService();

	public SkuService getSkuService();

	public BundleService getBundleService();

	public CoordinatorBundleData createBundle(Long userId, BundleCreationData bundleCreationData);

	public CoordinatorBundleData getBundleById(Long userId, Long bundleId);

	public List<CoordinatorBundleData> getBundles(Long userId);
}
