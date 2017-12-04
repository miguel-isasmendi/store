package com.store.domain.dao.client.impl;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.commons.lang.StringUtils;

import com.google.cloud.Timestamp;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Entity.Builder;
import com.google.cloud.datastore.FullEntity;
import com.google.cloud.datastore.IncompleteKey;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery.CompositeFilter;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;
import com.google.inject.Inject;
import com.store.architecture.exception.dao.ConflictDaoException;
import com.store.architecture.exception.dao.NotFoundDaoException;
import com.store.domain.dao.DaoConstants;
import com.store.domain.dao.client.ClientDao;
import com.store.domain.exception.ErrorConstants;
import com.store.domain.model.client.Client;
import com.store.domain.model.client.data.ClientCreationData;
import com.store.domain.model.client.data.ClientModificationData;

import lombok.NonNull;

public class DatastoreClientDao implements ClientDao {

	public static final String KIND = "Client";

	private static final String FIRST_NAME = "firstName";
	private static final String LAST_NAME = "lastName";

	private Datastore datastore;

	@Inject
	public DatastoreClientDao(Datastore datastore) {
		super();
		this.datastore = datastore;
	}

	@Override
	public Client create(@NonNull Long userId, @NonNull ClientCreationData clientData) {
		try {
			getByName(clientData.getFirstName(), clientData.getLastName());
			throw new ConflictDaoException(ErrorConstants.CLIENT_WITH_SAME_NAME_ALREADY_EXISTS);
		} catch (NotFoundDaoException exception) {
			FullEntity<IncompleteKey> entity = Entity.newBuilder()
					.setKey(datastore.allocateId(datastore.newKeyFactory().setKind(KIND).newKey()))
					.set(FIRST_NAME, clientData.getFirstName()).set(LAST_NAME, clientData.getLastName())
					.set(DaoConstants.EMAIL, clientData.getEmail()).set(DaoConstants.CREATED_BY_USER_ID, userId)
					.set(DaoConstants.CREATED_ON, Timestamp.now()).build();
			return hidrateFromEntity(datastore.put(entity));
		}

	}

	private Client hidrateFromEntity(Entity entity) {
		return Client.builder().clientId(entity.getKey().getId()).firstName(entity.getString(FIRST_NAME))
				.lastName(entity.getString(LAST_NAME)).email(entity.getString(DaoConstants.EMAIL))
				.createdByUserId(entity.getLong(DaoConstants.CREATED_BY_USER_ID))
				.createdOn(entity.getTimestamp(DaoConstants.CREATED_ON)).build();
	}

	@Override
	public Client getById(@NonNull Long clientId) {
		return hidrateFromEntity(getEntityById(clientId));
	}

	public Entity getEntityById(@NonNull Long clientId) {

		Key key = datastore.newKeyFactory().setKind(KIND).newKey(clientId);
		Query<Entity> query = Query.newEntityQueryBuilder().setKind(KIND)
				.setFilter(PropertyFilter.eq(DaoConstants.KEY_FIELD, key)).build();

		QueryResults<Entity> results = datastore.run(query);

		if (!results.hasNext()) {
			throw new NotFoundDaoException(ErrorConstants.formatError(ErrorConstants.ELEMENT_NOT_FOUND_FOR_ARGUMENTS,
					"Client", "clientId = " + clientId));
		}

		Entity entity = results.next();

		return entity;
	}

	@Override
	public List<Long> getClientsIds() {
		return StreamSupport
				.stream(Spliterators.spliteratorUnknownSize(
						datastore.run(Query.newKeyQueryBuilder().setKind(KIND).build()), 0), false)
				.map(Key::getId).collect(Collectors.toList());
	}

	public Client getByName(@NonNull String firstName, @NonNull String lastName) {
		Query<Entity> query = Query.newEntityQueryBuilder().setKind(KIND).setFilter(
				CompositeFilter.and(PropertyFilter.eq(FIRST_NAME, firstName), PropertyFilter.eq(LAST_NAME, lastName)))
				.build();
		try {
			Entity clientEntity = datastore.run(query).next();

			if (clientEntity == null) {
				throw new NotFoundDaoException(
						ErrorConstants.formatError(ErrorConstants.ELEMENT_NOT_FOUND_FOR_ARGUMENTS, "Client",
								"firstName = " + firstName + " lastName = " + lastName));
			}
			return hidrateFromEntity(clientEntity);
		} catch (NoSuchElementException exception) {
			throw new NotFoundDaoException(ErrorConstants.formatError(ErrorConstants.ELEMENT_NOT_FOUND_FOR_ARGUMENTS,
					"Client", "firstName = " + firstName + " lastName = " + lastName));
		}
	}

	@Override
	public Client delete(@NonNull Long clientId) {
		Key key = datastore.newKeyFactory().setKind(KIND).newKey(clientId);

		Query<Entity> query = Query.newEntityQueryBuilder().setKind(KIND)
				.setFilter(PropertyFilter.eq(DaoConstants.KEY_FIELD, key)).build();

		Entity clientEntityToDelete = datastore.run(query).next();

		datastore.delete(clientEntityToDelete.getKey());

		return hidrateFromEntity(clientEntityToDelete);
	}

	@Override
	public Client update(@NonNull ClientModificationData clientData) {

		Entity entity = getEntityById(clientData.getClientId());
		Client client = hidrateFromEntity(entity);

		Builder entityBuilder = Entity.newBuilder(entity);

		if (clientData.getFirstName() != null
				&& !StringUtils.equals(client.getFirstName(), clientData.getFirstName())) {
			entityBuilder.set(FIRST_NAME, clientData.getFirstName());
		}

		if (clientData.getEmail() != null && !StringUtils.equals(client.getEmail(), clientData.getEmail())) {
			entityBuilder.set(DaoConstants.EMAIL, clientData.getEmail());
		}

		if (clientData.getLastName() != null && !StringUtils.equals(client.getLastName(), clientData.getLastName())) {
			entityBuilder.set(LAST_NAME, clientData.getLastName());
		}

		entity = entityBuilder.build();

		datastore.update(entity);

		return hidrateFromEntity(entity);
	}
}
