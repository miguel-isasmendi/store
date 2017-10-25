package com.store.domain.architecture.configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.google.api.control.ServiceManagementConfigFilter;
import com.google.api.control.extensions.appengine.GoogleAppEngineControlFilter;
import com.google.api.server.spi.guice.EndpointsModule;
import com.google.common.collect.ImmutableList;
import com.google.inject.Scopes;
import com.google.inject.matcher.Matcher;
import com.google.inject.matcher.Matchers;
import com.store.architecture.exception.interceptor.ExceptionMapperInterceptor;
import com.store.architecture.filter.CorsFilter;
import com.store.architecture.filter.exception.CustomExceptionFilter;
import com.store.domain.api.admin.EmailSenderApi;
import com.store.domain.api.open.PublicRegistrationApi;
import com.store.domain.api.regular.CatalogApi;
import com.store.domain.api.regular.CheckoutApi;
import com.store.domain.api.regular.ClientApi;
import com.store.domain.api.regular.UserRegistrationApi;
import com.store.domain.api.regular.UsersApi;
import com.store.domain.architecture.api.regular.FirebaseRegularUserAuthenticationProtectedApi;
import com.store.domain.architecture.interceptor.FinishedRegistrationValidationInterceptor;

public class SystemResourcesLoaderModule extends EndpointsModule {
	private static final String WHOLE_API_PATH = "/_ah/api/*";
	private static final String DOMAIN_API_PATH = "/_ah/api/store/*";

	private Properties domainProperties;

	public SystemResourcesLoaderModule(Properties domainProperties) {
		this.domainProperties = domainProperties;
	}

	@Override
	public void configureServlets() {

		// GCloud filters
		createGCloudFilters(domainProperties.getProperty("endpoints.projectId"),
				domainProperties.getProperty("endpoints.serviceName"));

		// Custom filters
		addCustomFilters();

		// methods interceptor
		addCustomMethodInterceptors();

		// regular users APIs
		bind(UsersApi.class).in(Scopes.SINGLETON);
		bind(UserRegistrationApi.class).in(Scopes.SINGLETON);
		bind(ClientApi.class).in(Scopes.SINGLETON);
		bind(CatalogApi.class).in(Scopes.SINGLETON);
		bind(CheckoutApi.class).in(Scopes.SINGLETON);

		// public APIs
		bind(PublicRegistrationApi.class).in(Scopes.SINGLETON);

		// admin APIs
		bind(EmailSenderApi.class).in(Scopes.SINGLETON);

		configureEndpoints(WHOLE_API_PATH, ImmutableList.of(PublicRegistrationApi.class, UsersApi.class,
				UserRegistrationApi.class, EmailSenderApi.class, ClientApi.class, CatalogApi.class, CheckoutApi.class));

	}

	private void createGCloudFilters(String projectId, String serviceName) {
		bind(ServiceManagementConfigFilter.class).in(Scopes.SINGLETON);
		bind(GoogleAppEngineControlFilter.class).in(Scopes.SINGLETON);

		filter(WHOLE_API_PATH).through(ServiceManagementConfigFilter.class);

		Map<String, String> paramsControllerFilter = new HashMap<String, String>();
		paramsControllerFilter.put("endpoints.project.id", projectId);
		paramsControllerFilter.put("endpoints.serviceName", serviceName);
		filter(WHOLE_API_PATH).through(GoogleAppEngineControlFilter.class, paramsControllerFilter);
	}

	private void addCustomMethodInterceptors() {
		ExceptionMapperInterceptor exceptionInterceptor = new ExceptionMapperInterceptor();
		@SuppressWarnings("rawtypes")
		Matcher<Class> autenticatedClassesMatcher = Matchers
				.subclassesOf(FirebaseRegularUserAuthenticationProtectedApi.class);

		// Intercepting private APIs
		bindInterceptor(autenticatedClassesMatcher, Matchers.any(), exceptionInterceptor);

		// Intercepting public APIs
		bindInterceptor(Matchers.subclassesOf(PublicRegistrationApi.class), Matchers.any(), exceptionInterceptor);

		bindInterceptor(
				autenticatedClassesMatcher.and(Matchers.not(
						Matchers.subclassesOf(UserRegistrationApi.class).or(Matchers.subclassesOf(UsersApi.class)))),
				Matchers.any(), new FinishedRegistrationValidationInterceptor());
	}

	private void addCustomFilters() {
		bind(CustomExceptionFilter.class).in(Scopes.SINGLETON);
		filter(DOMAIN_API_PATH).through(CustomExceptionFilter.class);

		bind(CorsFilter.class).in(Scopes.SINGLETON);
	}
}