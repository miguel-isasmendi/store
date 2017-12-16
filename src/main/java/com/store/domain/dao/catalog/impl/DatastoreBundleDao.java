package com.store.domain.dao.catalog.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterators;
import java.util.logging.Logger;
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
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;
import com.google.cloud.datastore.Transaction;
import com.google.inject.Inject;
import com.store.architecture.exception.dao.NotFoundDaoException;
import com.store.domain.dao.DaoConstants;
import com.store.domain.dao.catalog.BundleDao;
import com.store.domain.exception.ErrorConstants;
import com.store.domain.model.bundle.Bundle;
import com.store.domain.model.bundle.Bundle.BundleBuilder;
import com.store.domain.model.bundle.BundleItem;
import com.store.domain.model.bundle.data.BundleCreationData;
import com.store.domain.model.bundle.data.BundleCreationItemData;

import lombok.NonNull;

public class DatastoreBundleDao implements BundleDao {

	private static final Logger logger = Logger.getLogger(DatastoreBundleDao.class.getName());

	private Datastore datastore;

	private static String KIND = "Bundle";
	private static String BUNDLE_ITEM_KIND = "BundleItem";

	private static String SKU_ID = "skuId";
	private static String BUNDLE_ID = "bundleId";

	@Inject
	public DatastoreBundleDao(Datastore datastore) {
		this.datastore = datastore;
	}

	@Override
	public Bundle create(@NonNull Long userId, @NonNull BundleCreationData bundleCreationData) {
		logger.info("Creating new bundle");
		Transaction txn = datastore.newTransaction();

		try {
			Key bundleKey = datastore.allocateId(datastore.newKeyFactory().setKind(KIND).newKey());

			logger.fine(String.format("bundle key allocated: %s", bundleKey.toString()));

			FullEntity<IncompleteKey> newIncompleteEntity = Entity.newBuilder().setKey(bundleKey)
					.setNull(DaoConstants.ACTIVE_UNTIL).set(DaoConstants.ACTIVE_FROM, Timestamp.now())
					.set(DaoConstants.CREATED_ON, Timestamp.now()).set(DaoConstants.CREATED_BY_USER_ID, userId).build();

			logger.fine("Building items");

			@SuppressWarnings("unchecked")
			FullEntity<IncompleteKey>[] items = new FullEntity[bundleCreationData.getItems().size()];
			for (int j = 0; j < items.length; j++) {

				BundleCreationItemData item = bundleCreationData.getItems().get(j);
				items[j] = Entity.newBuilder().setKey(datastore.newKeyFactory().setKind(KIND).newKey())
						.set(BUNDLE_ID, bundleKey.getId()).set(DaoConstants.QUANTITY, item.getQuantity())
						.set(SKU_ID, item.getSkuId()).set(DaoConstants.CREATED_ON, Timestamp.now())
						.set(DaoConstants.CREATED_BY_USER_ID, userId).build();
			}

			logger.fine("Saving entities items");

			Entity bundleEntity = datastore.put(newIncompleteEntity);
			List<Entity> savedItemsEntities = datastore.put(items);

			txn.commit();

			return hidrateEntity(bundleEntity, savedItemsEntities.iterator());
		} finally {
			if (txn.isActive()) {
				logger.severe("Executing rollback");
				txn.rollback();
			}
		}
	}

	private Bundle hidrateEntity(Entity entity, Iterator<Entity> itemsIterator) {
		logger.info("Hidrating entity");

		BundleBuilder builder = Bundle.builder().bundleId(entity.getKey().getId())
				.activeFrom(entity.getTimestamp(DaoConstants.ACTIVE_FROM))
				.activeUntil((Timestamp) entity.getValue(DaoConstants.ACTIVE_UNTIL).get())
				.createdByUserId(entity.getLong(DaoConstants.CREATED_BY_USER_ID))
				.createdOn(entity.getTimestamp(DaoConstants.CREATED_ON));

		Iterator<Entity> items = itemsIterator;

		if (items == null) {
			items = getItemEntitiesForBundleId(entity.getKey().getId());
		}

		logger.fine("Building items...");
		while (items.hasNext()) {
			Entity itemEntity = itemsIterator.next();

			builder.item(BundleItem.builder().bundleItemId(itemEntity.getKey().getId())
					.bundleId(itemEntity.getLong(BUNDLE_ID)).quantity(itemEntity.getLong(DaoConstants.QUANTITY))
					.skuId(itemEntity.getLong(SKU_ID)).createdOn(itemEntity.getTimestamp(DaoConstants.CREATED_ON))
					.createdByUserId(itemEntity.getLong(DaoConstants.CREATED_BY_USER_ID)).build());

		}

		logger.fine("Executing bundle on memory build process...");

		Bundle bundle = builder.build();

		logger.info("Bundle creation completed successfuly");

		return bundle;
	}

	@Override
	public Bundle getById(@NonNull Long bundleId) {
		Entity entity = getEntityById(bundleId);

		QueryResults<Entity> itemEntities = getItemEntitiesForBundleId(entity.getKey().getId());

		return hidrateEntity(entity, itemEntities);
	}

	public Entity getEntityById(@NonNull Long bundleId) {
		Entity entity = datastore.get(datastore.newKeyFactory().setKind(KIND).newKey(bundleId));

		if (entity == null) {
			throw new NotFoundDaoException(
					ErrorConstants.formatError(ErrorConstants.ELEMENT_NOT_FOUND_FOR_ARGUMENTS, KIND, "bundleId"));
		}

		return entity;
	}

	public QueryResults<Entity> getItemEntitiesForBundleId(@NonNull Long bundleId) {
		logger.info("Retrieving items from datastore...");

		return datastore.run(Query.newEntityQueryBuilder().setKind(BUNDLE_ITEM_KIND)
				.setFilter(PropertyFilter.eq(BUNDLE_ID, bundleId)).build());
	}

	@Override
	public Bundle delete(@NonNull Long bundleId) {
		logger.info(String.format("Searching inner key for bundleId = %s", bundleId));

		QueryResults<Key> entitiesKeys = datastore.run(Query.newKeyQueryBuilder().setKind(KIND).setFilter(
				PropertyFilter.eq(DaoConstants.KEY_FIELD, datastore.newKeyFactory().setKind(KIND).newKey(bundleId)))
				.build());

		if (!entitiesKeys.hasNext()) {
			throw new NotFoundDaoException(
					ErrorConstants.formatError(ErrorConstants.ELEMENT_NOT_FOUND_FOR_ARGUMENTS, KIND, "bundleId"));
		}

		Key bundleKey = entitiesKeys.next();

		logger.fine(String.format("Searching inner keys of items for bundleId = %s", bundleId));

		entitiesKeys = datastore.run(Query.newKeyQueryBuilder().setKind(BUNDLE_ITEM_KIND)
				.setFilter(PropertyFilter.eq(BUNDLE_ID, bundleId)).build());

		// TODO optimize the access to data and structure of the entities created
		Bundle bundle = getById(bundleId);

		List<Key> entitiesToDelete = new ArrayList<Key>();
		entitiesToDelete.add(bundleKey);

		while (entitiesKeys.hasNext()) {
			entitiesToDelete.add(entitiesKeys.next());
		}

		logger.fine(String.format("Deleting keys from datastore %s", entitiesToDelete.toString()));

		datastore.delete(entitiesToDelete.toArray(new Key[entitiesToDelete.size()]));

		logger.info("Bundle deletion completed successfuly!");

		return bundle;
	}

	@Override
	public List<Long> getBundlesIds() {
		return StreamSupport
				.stream(Spliterators.spliteratorUnknownSize(
						datastore.run(Query.newKeyQueryBuilder().setKind(KIND).build()), 0), false)
				.map(Key::getId).collect(Collectors.toList());
	}
}
