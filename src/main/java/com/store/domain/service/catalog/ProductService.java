package com.store.domain.service.catalog;

import java.util.List;

import com.store.domain.model.product.data.ProductCreationData;
import com.store.domain.model.product.data.ProductData;

public interface ProductService {

	List<ProductData> create(Long userId, ProductCreationData... prodDataArray);

	ProductData getById(Long productId);

	public List<ProductData> getProducts();

}
