package com.store.domain.dao.catalog.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.commons.lang3.StringUtils;

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
import com.store.architecture.exception.dao.ConflictDaoException;
import com.store.architecture.exception.dao.InvalidArgumentsDaoException;
import com.store.architecture.exception.dao.NotFoundDaoException;
import com.store.domain.dao.DaoConstants;
import com.store.domain.dao.catalog.SkuDao;
import com.store.domain.exception.ErrorConstants;
import com.store.domain.model.sku.Sku;
import com.store.domain.model.sku.data.SkuCreationData;

import lombok.NonNull;

public class DatastoreSkuDao implements SkuDao {

	private Datastore datastore;

	private static String SKU_KIND = "Sku";

	private static String SKU_PRODUCT_ID = "productId";
	private static String SKU_DEFAULT = "default";
	private static String SKU_MADE_INACTIVE_BY_SKU_ID = "madeInactiveBySkuId";

	@Inject
	public DatastoreSkuDao(Datastore datastore) {
		super();
		this.datastore = datastore;
	}

	private Sku hidrateSkuFromEntity(Entity entity) {
		return Sku.builder().skuId(entity.getKey().getId()).currency(entity.getString(DaoConstants.CURRENCY))
				.description(entity.getString(DaoConstants.DESCRIPTION)).price(entity.getDouble(DaoConstants.PRICE))
				.madeInactiveBySkuId((Long) entity.getValue(SKU_MADE_INACTIVE_BY_SKU_ID).get())
				.isDefault(entity.getBoolean(SKU_DEFAULT)).createdOn(entity.getTimestamp(DaoConstants.CREATED_ON))
				.createdByUserId(entity.getLong(DaoConstants.CREATED_BY_USER_ID))
				.productId(entity.getLong(SKU_PRODUCT_ID)).build();
	}

	private List<Sku> makeSkuList(Iterator<Entity> entities) {
		if (entities == null) {
			return Collections.emptyList();
		}
		return StreamSupport.stream(Spliterators.spliteratorUnknownSize(entities, 0), false)
				.map(this::hidrateSkuFromEntity).collect(Collectors.toList());
	}

	@Override
	public Sku create(@NonNull SkuCreationData skuData, @NonNull Long userId) {
		Transaction txn = datastore.newTransaction();

		try {

			// Deactivating other skus with the same code
			QueryResults<Entity> queryResults = datastore
					.run(Query.newEntityQueryBuilder().setKind(SKU_KIND)
							.setFilter(CompositeFilter.and(PropertyFilter.eq(SKU_PRODUCT_ID, skuData.getProductId()),
									PropertyFilter.eq(DaoConstants.KEY_FIELD,
											datastore.newKeyFactory().setKind(SKU_KIND).newKey(skuData.getSkuId()))))
							.build());

			if (!queryResults.hasNext()) {
				throw new NotFoundDaoException(ErrorConstants.formatError(
						ErrorConstants.ELEMENT_NOT_FOUND_FOR_ARGUMENTS, SKU_KIND,
						"skuId = \"" + skuData.getSkuId() + "\", " + "productId = \"" + skuData.getProductId() + "\""));
			}

			Entity alreadySavedSkuEntity = queryResults.next();

			Sku alreadySavedSku = makeSkuList(Arrays.asList(alreadySavedSkuEntity).iterator()).iterator().next();

			if (alreadySavedSku.getMadeInactiveBySkuId() != null) {
				throw new InvalidArgumentsDaoException(
						ErrorConstants.formatError(ErrorConstants.CANT_INVALIDATE_ELEMENT_UNTIL_ACTIVE, SKU_KIND));
			}

			// if they are really different, I modify the old version
			if (!StringUtils.equals(alreadySavedSku.getCurrency(), skuData.getCurrency())
					|| !alreadySavedSku.getPrice().equals(skuData.getPrice())) {

				// Saving new sku
				FullEntity<IncompleteKey> entity = Entity.newBuilder()
						.setKey(datastore.newKeyFactory().setKind(SKU_KIND).newKey())
						.set(DaoConstants.CURRENCY, skuData.getCurrency()).set(DaoConstants.PRICE, skuData.getPrice())
						.set(DaoConstants.DESCRIPTION, skuData.getDescription())
						.set(SKU_PRODUCT_ID, skuData.getProductId()).setNull(SKU_MADE_INACTIVE_BY_SKU_ID)
						.set(SKU_DEFAULT, alreadySavedSku.getIsDefault()).set(DaoConstants.CREATED_ON, Timestamp.now())
						.set(DaoConstants.CREATED_BY_USER_ID, userId).build();

				Entity savedEntity = datastore.put(entity);

				// Modifying old sku
				datastore.update(Entity.newBuilder(alreadySavedSkuEntity)
						.set(SKU_MADE_INACTIVE_BY_SKU_ID, savedEntity.getKey().getId()).build());

				txn.commit();

				return makeSkuList(Arrays.asList(savedEntity).iterator()).get(0);

			} else {
				throw new ConflictDaoException(
						ErrorConstants.formatError(ErrorConstants.SAME_ELEMENT_CONFLICT, SKU_KIND));
			}

		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}
	}

	@Override
	public Sku getById(@NonNull Long skuId) {
		Key key = datastore.newKeyFactory().setKind(SKU_KIND).newKey(skuId);
		Entity entity = datastore.get(key);

		if (entity == null) {
			throw new NotFoundDaoException(
					ErrorConstants.formatError(ErrorConstants.ELEMENT_NOT_FOUND_FOR_ARGUMENTS, SKU_KIND, "skuId"));
		}

		return makeSkuList(Arrays.asList(entity).iterator()).get(0);
	}

	@Override
	public List<Sku> getSkusByProductId(@NonNull Long productId) {
		Query<Entity> query = Query.newEntityQueryBuilder().setKind(SKU_KIND).setFilter(CompositeFilter
				.and(PropertyFilter.eq(SKU_PRODUCT_ID, productId), PropertyFilter.isNull(SKU_MADE_INACTIVE_BY_SKU_ID)))
				.build();

		return makeSkuList(datastore.run(query));
	}
}
