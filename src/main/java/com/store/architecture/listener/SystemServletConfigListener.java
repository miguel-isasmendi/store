package com.store.architecture.listener;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.store.domain.architecture.configuration.SystemRootModule;

public class SystemServletConfigListener extends GuiceServletContextListener {

	private static Injector injector;

	public static synchronized Injector getCreatedInjector() {
		if (injector == null) {
			injector = Guice.createInjector(new SystemRootModule());
		}

		return injector;
	}

	@Override
	protected synchronized Injector getInjector() {
		return getCreatedInjector();
	}
}