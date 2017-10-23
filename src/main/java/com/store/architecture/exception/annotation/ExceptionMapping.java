package com.store.architecture.exception.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.google.api.client.http.HttpStatusCodes;
import com.google.api.server.spi.ServiceException;

/**
 * <p>
 * This annotation stores an exception mapping that will occur for all that
 * methods that are affected by {@link CactusExceptionMapperInterceptor}
 * providing required information to build the {@link ServiceException} that
 * will be thrown.
 * </p>
 * <p>
 * This annotation supports repetition by converting repetitions to
 * {@link ExceptionMappings}.
 * </p>
 */
@Documented
@Retention(RUNTIME)
@Target({ TYPE, METHOD })
@Repeatable(ExceptionMappings.class)
public @interface ExceptionMapping {
	Class<? extends RuntimeException> from();

	Class<? extends Throwable> to();

	String message() default "";

	int statusCode() default HttpStatusCodes.STATUS_CODE_SERVER_ERROR;
}
