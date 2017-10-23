package com.store.domain.dao.user.impl;

import com.google.cloud.Timestamp;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Entity.Builder;
import com.google.cloud.datastore.FullEntity;
import com.google.cloud.datastore.IncompleteKey;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;
import com.google.inject.Inject;
import com.store.architecture.exception.dao.NotFoundDaoException;
import com.store.domain.dao.DaoConstants;
import com.store.domain.dao.user.UserDao;
import com.store.domain.exception.ErrorConstants;
import com.store.domain.model.user.User;
import com.store.domain.model.user.UserStatus;
import com.store.domain.model.user.data.UserCreationData;
import com.store.domain.model.user.data.UserModificationData;

import lombok.NonNull;

public class DatastoreUserDao implements UserDao {

	public static final String KIND = "User";

	public static final String FIRST_NAME = "firstName";
	public static final String LAST_NAME = "lastName";
	public static final String FIREBASE_ID = "firebaseId";

	private Datastore datastore;

	@Inject
	public DatastoreUserDao(Datastore datastore) {
		this.datastore = datastore;
	}

	@Override
	public User create(@NonNull UserCreationData userCreationData) {

		FullEntity<IncompleteKey> entity = Entity.newBuilder()
				.setKey(datastore.allocateId(datastore.newKeyFactory().setKind(KIND).newKey()))
				.set(DaoConstants.STATUS, UserStatus.NEW.toString()).set(FIRST_NAME, userCreationData.getFirstName())
				.set(LAST_NAME, userCreationData.getLastName()).set(DaoConstants.EMAIL, userCreationData.getEmail())
				.set(FIREBASE_ID, userCreationData.getFirebaseId()).set(DaoConstants.CREATED_ON, Timestamp.now())
				.build();

		Entity userEntity = datastore.put(entity);

		userEntity = Entity.newBuilder(userEntity).set(DaoConstants.CREATED_BY_USER_ID, userEntity.getKey().getId())
				.build();

		datastore.update(userEntity);

		return hidrateFromEntity(userEntity);
	}

	@Override
	public User getByfirebaseId(String firebaseId) {
		Query<Entity> query = Query.newEntityQueryBuilder().setKind(KIND)
				.setFilter(PropertyFilter.eq(FIREBASE_ID, firebaseId)).build();

		QueryResults<Entity> qEntity = datastore.run(query);
		if (qEntity != null && qEntity.hasNext()) {
			return hidrateFromEntity(qEntity.next());
		} else {
			throw new NotFoundDaoException(ErrorConstants.formatError(ErrorConstants.ELEMENT_NOT_FOUND_FOR_ARGUMENTS,
					"User", "firebaseId = " + firebaseId));
		}
	}

	User hidrateFromEntity(Entity entity) {
		User user = User.builder().userId(entity.getKey().getId())
				.status(UserStatus.valueOf(entity.getString(DaoConstants.STATUS)))
				.email(entity.getString(DaoConstants.EMAIL)).firstName(entity.getString(FIRST_NAME))
				.lastName(entity.getString(LAST_NAME)).firebaseId(entity.getString(FIREBASE_ID))
				.createdOn(entity.getTimestamp(DaoConstants.CREATED_ON)).build();
		return user;
	}

	@Override
	public User update(@NonNull UserModificationData newUserData) {
		Builder updatedUserEntityBuilder = Entity.newBuilder(getEntityById(newUserData.getUserId()));

		if (newUserData.getStatus() != null) {
			updatedUserEntityBuilder.set(DaoConstants.STATUS, newUserData.getStatus().toString());
		}

		Entity updatedUserEntity = updatedUserEntityBuilder.build();
		datastore.update(updatedUserEntity);

		return hidrateFromEntity(updatedUserEntity);
	}

	private Entity getEntityById(@NonNull Long userId) {
		Query<Entity> query = Query.newEntityQueryBuilder().setKind(KIND).setFilter(
				PropertyFilter.eq(DaoConstants.KEY_FIELD, datastore.newKeyFactory().setKind(KIND).newKey(userId)))
				.build();

		QueryResults<Entity> qEntity = datastore.run(query);
		if (qEntity != null && qEntity.hasNext()) {
			return qEntity.next();
		} else {
			throw new NotFoundDaoException(ErrorConstants.formatError(ErrorConstants.ELEMENT_NOT_FOUND_FOR_ARGUMENTS,
					"User", "userId = " + userId));
		}
	}

	@Override
	public User getUserById(@NonNull Long userId) {
		return hidrateFromEntity(getEntityById(userId));
	}

	@Override
	public void delete(Long userId) {
		Key key = datastore.newKeyFactory().setKind(KIND).newKey(userId);
		datastore.delete(key);
	}
}