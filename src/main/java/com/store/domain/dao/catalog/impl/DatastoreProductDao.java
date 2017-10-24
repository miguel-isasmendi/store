package com.store.domain.dao.catalog.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.google.cloud.Timestamp;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.FullEntity;
import com.google.cloud.datastore.IncompleteKey;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery.CompositeFilter;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;
import com.google.cloud.datastore.Transaction;
import com.google.inject.Inject;
import com.store.architecture.exception.dao.NotFoundDaoException;
import com.store.domain.dao.DaoConstants;
import com.store.domain.dao.catalog.ProductDao;
import com.store.domain.exception.ErrorConstants;
import com.store.domain.model.product.Product;
import com.store.domain.model.product.data.ProductCreationData;

import lombok.NonNull;

public class DatastoreProductDao implements ProductDao {

	private Datastore datastore;

	protected static String PRODUCT_KIND = "Product";

	@Inject
	public DatastoreProductDao(Datastore datastore) {
		super();
		this.datastore = datastore;
	}

	@Override
	public List<Product> create(@NonNull Long userId, @NonNull ProductCreationData... prodDataArray) {
		Transaction txn = datastore.newTransaction();

		try {

			List<Product> productsResult = new ArrayList<Product>(prodDataArray.length);

			for (ProductCreationData prodData : prodDataArray) {
				FullEntity<IncompleteKey> entity = Entity.newBuilder()
						.setKey(datastore.allocateId(datastore.newKeyFactory().setKind(PRODUCT_KIND).newKey()))
						.set(DaoConstants.NAME, prodData.getName())
						.set(DaoConstants.DESCRIPTION, prodData.getDescription())
						.set(DaoConstants.CREATED_BY_USER_ID, userId).set(DaoConstants.CREATED_ON, Timestamp.now())
						.build();
				Entity product = datastore.put(entity);

				productsResult.add(hidrateProductFromEntity(product));
			}

			txn.commit();

			return productsResult;

		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}
	}

	private Product hidrateProductFromEntity(Entity entity) {
		return Product.builder().productId(entity.getKey().getId())
				.description(entity.getString(DaoConstants.DESCRIPTION))
				.createdByUserId(entity.getLong(DaoConstants.CREATED_BY_USER_ID))
				.createdOn(entity.getTimestamp(DaoConstants.CREATED_ON)).name(entity.getString(DaoConstants.NAME))
				.build();
	}

	@Override
	public Product getById(@NonNull Long productId) {
		Key key = datastore.newKeyFactory().setKind(PRODUCT_KIND).newKey(productId);
		QueryResults<Entity> entityResults = datastore.run(Query.newEntityQueryBuilder().setKind(PRODUCT_KIND)
				.setFilter(CompositeFilter.and(PropertyFilter.eq(DaoConstants.KEY_FIELD, key))).build());

		if (!entityResults.hasNext()) {
			throw new NotFoundDaoException(ErrorConstants.formatError(ErrorConstants.ELEMENT_NOT_FOUND_FOR_ARGUMENTS,
					"Product", "productId = \"" + productId + "\""));
		}

		return makeList(entityResults).get(0);
	}

	@Override
	public List<Product> getProducts() {
		Query<Entity> query = Query.newEntityQueryBuilder().setKind(PRODUCT_KIND).build();

		QueryResults<Entity> entities = datastore.run(query);

		return makeList(entities);
	}

	private List<Product> makeList(Iterator<Entity> entities) {
		if (entities == null) {
			return Collections.emptyList();
		}

		return StreamSupport.stream(Spliterators.spliteratorUnknownSize(entities, 0), false)
				.map(this::hidrateProductFromEntity).collect(Collectors.toList());
	}

}
