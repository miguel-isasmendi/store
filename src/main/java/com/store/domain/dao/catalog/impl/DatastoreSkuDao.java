package com.store.domain.dao.catalog.impl;

import java.util.List;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.google.cloud.Timestamp;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.FullEntity.Builder;
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
import com.store.domain.model.sku.SkuBillingType;
import com.store.domain.model.sku.data.SkuCreationData;

import lombok.NonNull;

public class DatastoreSkuDao implements SkuDao {

	private Datastore datastore;

	private static String KIND = "Sku";

	private static String SKU_PRODUCT_ID = "productId";
	private static String SKU_MADE_INACTIVE_BY_SKU_ID = "madeInactiveBySkuId";
	private static String SKU_BUNDLE_ID = "bundleId";
	private static String SKU_BILLING_TYPE = "billingType";

	@Inject
	public DatastoreSkuDao(@NonNull Datastore datastore) {
		super();
		this.datastore = datastore;
	}

	private Sku hidrateFromEntity(Entity entity) {
		return Sku.builder().skuId(entity.getKey().getId()).description(entity.getString(DaoConstants.DESCRIPTION))
				.price(entity.getDouble(DaoConstants.PRICE)).name(entity.getString(DaoConstants.NAME))
				.madeInactiveBySkuId((Long) entity.getValue(SKU_MADE_INACTIVE_BY_SKU_ID).get())
				.isDefault(entity.getBoolean(DaoConstants.DEFAULT))
				.createdOn(entity.getTimestamp(DaoConstants.CREATED_ON))
				.createdByUserId(entity.getLong(DaoConstants.CREATED_BY_USER_ID))
				.bundleId((Long) entity.getValue(SKU_BUNDLE_ID).get()).productId(entity.getLong(SKU_PRODUCT_ID))
				.billingType(SkuBillingType.valueOf(entity.getString(SKU_BILLING_TYPE))).build();
	}

	@Override
	public Sku create(@NonNull SkuCreationData skuData, @NonNull Long userId) {
		Transaction txn = datastore.newTransaction();

		try {
			if (skuData.getSkuId() == null) {
				return hidrateFromEntity(saveNewSku(userId, skuData, Boolean.FALSE, null));
			} else {
				// Deactivating other skus with the same code
				QueryResults<Entity> queryResults = datastore
						.run(Query.newEntityQueryBuilder().setKind(KIND)
								.setFilter(CompositeFilter.and(
										PropertyFilter.eq(SKU_PRODUCT_ID, skuData.getProductId()),
										PropertyFilter.eq(DaoConstants.KEY_FIELD,
												datastore.newKeyFactory().setKind(KIND).newKey(skuData.getSkuId()))))
								.build());

				if (!queryResults.hasNext()) {
					throw new NotFoundDaoException(ErrorConstants
							.formatError(ErrorConstants.ELEMENT_NOT_FOUND_FOR_ARGUMENTS, KIND, "skuId = \""
									+ skuData.getSkuId() + "\", " + "productId = \"" + skuData.getProductId() + "\""));
				}

				Entity alreadySavedSkuEntity = queryResults.next();

				Sku alreadySavedSku = hidrateFromEntity(alreadySavedSkuEntity);

				if (alreadySavedSku.getMadeInactiveBySkuId() != null) {
					throw new InvalidArgumentsDaoException(
							ErrorConstants.formatError(ErrorConstants.CANT_INVALIDATE_ELEMENT_UNTIL_ACTIVE, KIND));
				}

				// if they are really different, I modify the old version
				if (!alreadySavedSku.getPrice().equals(skuData.getPrice())) {

					Entity savedEntity = saveNewSku(userId, skuData, alreadySavedSku.getIsDefault(),
							alreadySavedSku.getBundleId());

					// Modifying old sku
					datastore.update(Entity.newBuilder(alreadySavedSkuEntity)
							.set(SKU_MADE_INACTIVE_BY_SKU_ID, savedEntity.getKey().getId()).build());

					txn.commit();

					return hidrateFromEntity(savedEntity);

				} else {
					throw new ConflictDaoException(
							ErrorConstants.formatError(ErrorConstants.SAME_ELEMENT_CONFLICT, KIND));
				}
			}

		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}
	}

	private Entity saveNewSku(@NonNull Long userId, @NonNull SkuCreationData skuData, @NonNull Boolean isDefault,
			Long oldBundleId) {
		Builder<IncompleteKey> builder = Entity.newBuilder().setKey(datastore.newKeyFactory().setKind(KIND).newKey())
				.set(DaoConstants.PRICE, skuData.getPrice()).set(DaoConstants.DESCRIPTION, skuData.getDescription())
				.set(DaoConstants.NAME, skuData.getName()).set(SKU_PRODUCT_ID, skuData.getProductId())
				.setNull(SKU_MADE_INACTIVE_BY_SKU_ID).set(DaoConstants.DEFAULT, isDefault)
				.set(DaoConstants.CREATED_ON, Timestamp.now()).set(DaoConstants.CREATED_BY_USER_ID, userId)
				.set(SKU_BILLING_TYPE, skuData.getBillingType().toString());

		if (skuData.getBundleId() == null) {
			if (oldBundleId != null) {
				builder.set(SKU_BUNDLE_ID, oldBundleId);
			} else {
				builder.setNull(SKU_BUNDLE_ID);
			}
		} else {
			builder.set(SKU_BUNDLE_ID, skuData.getBundleId());
		}

		return datastore.put(builder.build());
	}

	@Override
	public Sku getById(@NonNull Long skuId) {
		Key key = datastore.newKeyFactory().setKind(KIND).newKey(skuId);
		Entity entity = datastore.get(key);

		if (entity == null) {
			throw new NotFoundDaoException(
					ErrorConstants.formatError(ErrorConstants.ELEMENT_NOT_FOUND_FOR_ARGUMENTS, KIND, "skuId"));
		}

		return hidrateFromEntity(entity);
	}

	@Override
	public List<Long> getSkusIdsByProductId(@NonNull Long productId) {
		return StreamSupport
				.stream(Spliterators.spliteratorUnknownSize(datastore.run(Query.newKeyQueryBuilder().setKind(KIND)
						.setFilter(CompositeFilter.and(PropertyFilter.eq(SKU_PRODUCT_ID, productId),
								PropertyFilter.isNull(SKU_MADE_INACTIVE_BY_SKU_ID)))
						.build()), 0), false)
				.map(Key::getId).collect(Collectors.toList());
	}

	@Override
	public Sku getByBundleId(@NonNull Long bundleId) {
		Key key = datastore.newKeyFactory().setKind(KIND).newKey(bundleId);

		QueryResults<Entity> entities = datastore.run(Query.newEntityQueryBuilder().setKind(KIND)
				.setFilter(PropertyFilter.eq(SKU_BUNDLE_ID, bundleId)).build());

		if (!entities.hasNext()) {
			throw new NotFoundDaoException(
					ErrorConstants.formatError(ErrorConstants.ELEMENT_NOT_FOUND_FOR_ARGUMENTS, KIND, "skuId"));
		}

		return hidrateFromEntity(entities.next());
	}
}
