package com.store.architecture.data;

import java.util.logging.Logger;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.store.domain.architecture.configuration.SystemRootModule;
import com.store.domain.service.migration.MigrationService;

public class SchemaModificationProcessRunner {
	private static final Logger logger = Logger.getLogger(SchemaModificationProcessRunner.class.getName());

	public static void main(String[] args) {

		logger.info("Starting migration environment ...");
		Injector injector = Guice.createInjector(new SystemRootModule());
		MigrationService migrationService = injector.getInstance(MigrationService.class);
		logger.info("ok!");

		logger.info("Calling migration service ...");
		migrationService.migrateData();
		logger.info("Finished!");

		return;
	}
}
