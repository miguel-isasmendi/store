package com.store.domain.service.migration.impl;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.inject.Inject;
import com.store.domain.architecture.dao.schema.MigrationDao;
import com.store.domain.service.migration.MigrationService;

public class MigrationServiceImpl implements MigrationService {
	private MigrationDao migrationDao;

	@Inject
	public MigrationServiceImpl(MigrationDao migrationDao, MemcacheService cache) {
		super();
		this.migrationDao = migrationDao;
	}

	@Override
	public void migrateData() {
		migrationDao.toString();
	}

}
