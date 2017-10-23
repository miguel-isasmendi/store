package com.store.domain.service.catalog;

import java.util.List;

import com.store.domain.model.product.data.FullProductData;
import com.store.domain.model.product.data.ProductCreationData;
import com.store.domain.model.product.data.ProductData;
import com.store.domain.model.sku.data.SkuCreationData;
import com.store.domain.model.sku.data.SkuData;

public interface CatalogCoordinatorService {

	public List<FullProductData> createProduct(Long userId, ProductCreationData... prodDataArray);

	public FullProductData getProductById(Long productId);

	public List<FullProductData> getFullProducts();

	public List<ProductData> getProducts();

	public SkuData createSku(Long userId, SkuCreationData skuData);

	public SkuData getSkuById(Long skuId);
}
