package com.store.domain.architecture.configuration;

import java.io.IOException;
import java.util.Properties;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

/**
 * Root module in charge of loading environment-dependent properties and making
 * them available by name so they can be used afterwards by any other injection.
 *
 */
public class SystemRootModule extends AbstractModule {

	@Override
	protected void configure() {

		Properties properties = new Properties();
		try {
			properties.load(this.getClass().getClassLoader().getResourceAsStream("env.properties"));
			Names.bindProperties(binder(), properties);

			properties.load(this.getClass().getClassLoader().getResourceAsStream("config.properties"));
			Names.bindProperties(binder(), properties);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("unable to load properties file", e);
		}

		install(new SystemServiceAndDaoConfigurationsModule());
		install(new SystemResourcesLoaderModule(properties));
	}
}