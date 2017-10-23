package com.store.domain.architecture.dao.schema.impl;

import java.util.logging.Logger;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.inject.Inject;
import com.store.domain.architecture.dao.schema.MigrationDao;
import com.store.domain.dao.user.impl.DatastoreUserDao;

public class DatastoreMigrationDao implements MigrationDao {
	private static final Logger logger = Logger.getLogger(DatastoreMigrationDao.class.getName());

	@Inject
	private Datastore datastore;

	public void deleteUserByMail(String email) {
		logger.info("Migrating users");

		QueryResults<Entity> usersEntities = datastore
				.run(Query.newEntityQueryBuilder().setKind(DatastoreUserDao.KIND).build());

		while (usersEntities.hasNext()) {
			Entity user = usersEntities.next();

			datastore.delete(user.getKey());
			logger.info("End of processing for userId = " + user.getKey().getId());
			logger.info("==============================================");
		}

		logger.info("End of users migration");
	}

}
