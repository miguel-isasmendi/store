package com.store.domain.dao.registration.impl;

import com.google.cloud.Timestamp;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.FullEntity;
import com.google.cloud.datastore.IncompleteKey;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery.CompositeFilter;
import com.google.cloud.datastore.StructuredQuery.OrderBy;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;
import com.google.inject.Inject;
import com.store.architecture.exception.dao.NotFoundDaoException;
import com.store.architecture.utils.DateUtils;
import com.store.domain.dao.DaoConstants;
import com.store.domain.dao.registration.UserPendingValidationCodeDao;
import com.store.domain.exception.ErrorConstants;
import com.store.domain.model.validationCode.VerificationCode;
import com.store.domain.model.validationCode.data.VerificationCodeCreationData;

import lombok.NonNull;

public class DatastoreUserPendingValidationCodeDao implements UserPendingValidationCodeDao {

	private final static String KIND = "PendingValidationCode";

	private final static String CODE = "code";

	@Inject
	private Datastore datastore;

	@Override
	public VerificationCode createVerificationCode(@NonNull VerificationCodeCreationData verificationCodeCreationData) {
		FullEntity<IncompleteKey> entity = Entity.newBuilder()
				.setKey(datastore.allocateId(datastore.newKeyFactory().setKind(KIND).newKey()))
				.set(CODE, verificationCodeCreationData.getCode())
				.set(DaoConstants.USER_ID, verificationCodeCreationData.getUserId())
				.set(DaoConstants.CREATED_ON, Timestamp.now()).set(DaoConstants.VALID_UNTIL_DATE,
						DateUtils.timestampFrom(verificationCodeCreationData.getValidUntilDate()))
				.build();
		return hidrateFromEntity(datastore.put(entity));
	}

	private VerificationCode hidrateFromEntity(Entity entity) {
		return VerificationCode.builder().verificationCodeId(entity.getKey().getId())
				.userId(entity.getLong(DaoConstants.USER_ID)).createdOn(entity.getTimestamp(DaoConstants.CREATED_ON))
				.validUntilDate(entity.getTimestamp(DaoConstants.VALID_UNTIL_DATE)).code(entity.getString(CODE))
				.build();
	}

	@Override
	public VerificationCode getLastVerificationCode(@NonNull Long userId) {
		QueryResults<Entity> queryResults = datastore.run(Query.newEntityQueryBuilder().setKind(KIND)
				.setFilter(CompositeFilter.and(PropertyFilter.eq(DaoConstants.USER_ID, userId)))
				.setOrderBy(OrderBy.desc(DaoConstants.VALID_UNTIL_DATE)).build());

		if (!queryResults.hasNext()) {
			throw new NotFoundDaoException(ErrorConstants.formatError(ErrorConstants.ELEMENT_NOT_FOUND_FOR_ARGUMENTS,
					"Código de verificación", "userId = " + userId));
		}

		return hidrateFromEntity(queryResults.next());
	}

	@Override
	public void deleteValidationCodesFor(Long userId) {
		QueryResults<Entity> queryResults = datastore.run(Query.newEntityQueryBuilder().setKind(KIND)
				.setFilter(PropertyFilter.eq(DaoConstants.USER_ID, userId)).build());

		while (queryResults.hasNext()) {
			datastore.delete(queryResults.next().getKey());
		}

	}
}
