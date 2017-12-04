package com.store.domain.dao.catalog;

import java.util.List;

import com.store.domain.model.product.Product;
import com.store.domain.model.product.data.ProductCreationData;

public interface ProductDao {

	public List<Product> create(Long userId, ProductCreationData... prodDataArray);

	public Product getById(Long productId);

	public List<Long> getProductsIds();
}
