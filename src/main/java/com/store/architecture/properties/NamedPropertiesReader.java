package com.store.architecture.properties;

import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import com.store.architecture.listener.SystemServletConfigListener;

public final class NamedPropertiesReader {
	public static <T> T getProperty(Class<T> type, String propertyName) {
		return SystemServletConfigListener.getCreatedInjector()
				.getBinding(Key.get(TypeLiteral.get(type), Names.named(propertyName))).getProvider().get();
	}
}
