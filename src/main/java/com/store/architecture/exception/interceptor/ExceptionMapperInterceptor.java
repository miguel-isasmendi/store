package com.store.architecture.exception.interceptor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.google.api.server.spi.ServiceException;
import com.store.architecture.exception.annotation.ExceptionMapping;
import com.store.architecture.exception.annotation.ExceptionMappings;

/**
 * <p>
 * This method intercepter searches for definitions of {@link ExceptionMapping}
 * in the method or class to create an instance of a proper exception that holds
 * meaning for REST resources semantic's.
 * </p>
 * 
 * <p>
 * It would override the same mapping definition of a certain exception defined
 * in a class with another mapping defined for the same exception class in the
 * method, if it exists.
 * </p>
 * 
 * <p>
 * The handling of the exception mapping will be held in the same way that a
 * try/catch block will do, specially regarding declaration order.
 * </p>
 * 
 * <p>
 * The exceptions not taking part in the mapping definitions are thrown again as
 * they are, without changes.
 * </p>
 */
public class ExceptionMapperInterceptor implements MethodInterceptor {

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		try {
			return invocation.proceed();
		} catch (final RuntimeException exception) {

			Map<Class<? extends Object>, ExceptionMapping> mappingsFound = retrieveMappingsFromClass(
					invocation.getMethod().getDeclaringClass());

			Map<Class<? extends Object>, ExceptionMapping> methodMappingsFound = retrieveOverrideMappingsWithMethodDefinition(
					invocation.getMethod());

			for (Map.Entry<Class<? extends Object>, ExceptionMapping> entry : methodMappingsFound.entrySet()) {
				mappingsFound.put(entry.getKey(), entry.getValue());
			}

			Optional<Class<? extends Object>> exceptionClassFound = mappingsFound.keySet().stream().sequential()
					.filter(exceptionMappingFromClass -> exception.getClass()
							.isAssignableFrom((Class<? extends Object>) exceptionMappingFromClass))
					.limit(1).findFirst();

			Throwable processedException = exceptionClassFound.map(mappingsFound::get).map(mappingFound -> {
				try {
					if (ServiceException.class.equals(mappingFound.to())) {
						// We assume that the exception to throw is generic, so we've been provided with
						// the proper arguments to generate it
						return mappingFound.to().getConstructor(int.class, String.class, Throwable.class)
								.newInstance(mappingFound.statusCode(),
										(StringUtils.stripToEmpty(mappingFound.message()).isEmpty())
												? exception.getMessage()
												: mappingFound.message(),
										exception);
					} else {
						return mappingFound.to().getConstructor(Throwable.class).newInstance(exception);
					}
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException | NoSuchMethodException | SecurityException e) {
					return e;
				}
			}).orElse(exception);

			throw processedException;
		}
	}

	public LinkedHashMap<Class<? extends Object>, ExceptionMapping> retrieveMappingsFromClass(
			Class<? extends Object> classToScan) {
		List<ExceptionMapping> mappingsFound = new ArrayList<ExceptionMapping>();

		ExceptionMapping exceptionMapping = classToScan.getAnnotation(ExceptionMapping.class);

		if (exceptionMapping != null) {
			mappingsFound.add(exceptionMapping);
		} else {
			ExceptionMappings exceptionMappings = classToScan.getAnnotation(ExceptionMappings.class);

			if (exceptionMappings != null) {
				CollectionUtils.addAll(mappingsFound, exceptionMappings.value());
			}
		}

		return mappingsFound.stream().collect(Collectors.toMap(mapping -> mapping.from(), Function.identity(),
				(oldValue, newValue) -> newValue, LinkedHashMap::new));
	}

	public LinkedHashMap<Class<? extends Object>, ExceptionMapping> retrieveOverrideMappingsWithMethodDefinition(
			Method method) {
		List<ExceptionMapping> mappingsFound = new ArrayList<ExceptionMapping>();

		ExceptionMapping exceptionMapping = method.getAnnotation(ExceptionMapping.class);

		if (exceptionMapping != null) {
			mappingsFound.add(exceptionMapping);
		} else {
			ExceptionMappings exceptionMappings = method.getAnnotation(ExceptionMappings.class);

			if (exceptionMappings != null) {
				CollectionUtils.addAll(mappingsFound, exceptionMappings.value());
			}
		}

		return mappingsFound.stream().collect(Collectors.toMap(mapping -> mapping.from(), Function.identity(),
				(oldValue, newValue) -> newValue, LinkedHashMap::new));
	}
}
