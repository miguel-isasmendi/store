package com.store.architecture.exception.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.sun.jndi.cosnaming.ExceptionMapper;

/**
 * This is the holder of the repetitions that {@link ExceptionMapper} could
 * have.
 */
@Documented
@Retention(RUNTIME)
@Target({ TYPE, METHOD })
public @interface ExceptionMappings {
	ExceptionMapping[] value();
}
