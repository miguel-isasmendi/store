package com.store.domain.architecture.configuration;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.store.domain.architecture.dao.schema.MigrationDao;
import com.store.domain.architecture.dao.schema.impl.DatastoreMigrationDao;
import com.store.domain.dao.registration.UserPendingValidationCodeDao;
import com.store.domain.dao.registration.impl.DatastoreUserPendingValidationCodeDao;
import com.store.domain.dao.user.UserDao;
import com.store.domain.dao.user.impl.DatastoreUserDao;
import com.store.domain.service.email.EmailService;
import com.store.domain.service.email.impl.SendGridMailService;
import com.store.domain.service.firebase.FirebaseService;
import com.store.domain.service.firebase.impl.FirebaseServiceImpl;
import com.store.domain.service.migration.MigrationService;
import com.store.domain.service.migration.impl.MigrationServiceImpl;
import com.store.domain.service.registration.UserRegistrationCoordinatorService;
import com.store.domain.service.registration.UserRegistrationService;
import com.store.domain.service.registration.impl.UserRegistrationCoordinatorServiceImpl;
import com.store.domain.service.registration.impl.UserRegistrationServiceImpl;
import com.store.domain.service.user.UserService;
import com.store.domain.service.user.impl.CachedUserService;

public class SystemServiceAndDaoConfigurationsModule extends AbstractModule {

	@Override
	protected void configure() {

		bind(UserService.class).to(CachedUserService.class).in(Scopes.SINGLETON);
		bind(EmailService.class).to(SendGridMailService.class).in(Scopes.SINGLETON);

		bind(UserRegistrationService.class).to(UserRegistrationServiceImpl.class).in(Scopes.SINGLETON);
		bind(UserRegistrationCoordinatorService.class).to(UserRegistrationCoordinatorServiceImpl.class)
				.in(Scopes.SINGLETON);

		// Migration service
		// TODO should this be here?
		bind(MigrationService.class).to(MigrationServiceImpl.class).in(Scopes.SINGLETON);

		// internal services
		bind(URLFetchService.class).toInstance(URLFetchServiceFactory.getURLFetchService());
		bind(FirebaseService.class).to(FirebaseServiceImpl.class).in(Scopes.SINGLETON);

		bind(Gson.class).toInstance(new GsonBuilder().create());

		// dao layer
		bind(UserDao.class).to(DatastoreUserDao.class).in(Scopes.SINGLETON);
		bind(UserPendingValidationCodeDao.class).to(DatastoreUserPendingValidationCodeDao.class).in(Scopes.SINGLETON);

		// Migration dao
		// TODO should this be here?
		bind(MigrationDao.class).to(DatastoreMigrationDao.class).in(Scopes.SINGLETON);

		// storage
		bind(Datastore.class).toInstance(DatastoreOptions.getDefaultInstance().getService());
		bind(MemcacheService.class).toInstance(MemcacheServiceFactory.getMemcacheService());
	}
}