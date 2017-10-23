package com.store.architecture.exception.interceptor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.aopalliance.intercept.MethodInterceptor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.api.server.spi.ServiceException;
import com.google.api.server.spi.response.BadRequestException;
import com.google.appengine.repackaged.com.google.api.client.http.HttpStatusCodes;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Scopes;
import com.store.architecture.exception.annotation.ExceptionMapping;

@RunWith(MockitoJUnitRunner.class)
public class ExceptionMapperInterceptorTest extends AbstractModule {
	private static final String CLASS_SERVICE_EXCEPTION_MESSAGE = "Class exception";
	private static final String METHOD_SERVICE_EXCEPTION_MESSAGE = "Method exception";

	private InterceptedClass interceptedClassInstance;
	private ChildInterceptedClass childInterceptedClassInstance;

	@Before
	public void setUp() {
		Injector injector = Guice.createInjector(this);

		interceptedClassInstance = injector.getInstance(InterceptedClass.class);
		childInterceptedClassInstance = injector.getInstance(ChildInterceptedClass.class);
	}

	@After
	public void tearDown() {
	}

	@Test(expected = BadRequestException.class)
	public void getClassMappingOverridedByMedhodMappingTest() {
		interceptedClassInstance.getNullPointerOverridenException();
	}

	@Test(expected = ServiceException.class)
	public void getFullClassMappingTest() {
		try {
			interceptedClassInstance.getNullPointerClassMappedException();
		} catch (Throwable e) {
			assertTrue(ServiceException.class.isAssignableFrom(e.getClass()));

			ServiceException exception = (ServiceException) e;

			assertEquals(CLASS_SERVICE_EXCEPTION_MESSAGE, exception.getMessage());
			assertEquals(HttpStatusCodes.STATUS_CODE_CONFLICT, exception.getStatusCode());

			throw e;
		}
	}

	@Test(expected = ServiceException.class)
	public void getClassMappingWithOnlyInheritedMessageTest() {
		try {
			interceptedClassInstance.getIllegalArgumentClassMappedException();
		} catch (Throwable e) {
			assertTrue(ServiceException.class.isAssignableFrom(e.getClass()));

			ServiceException exception = (ServiceException) e;

			assertEquals(METHOD_SERVICE_EXCEPTION_MESSAGE, exception.getMessage());
			assertEquals(HttpStatusCodes.STATUS_CODE_BAD_REQUEST, exception.getStatusCode());

			throw e;
		}
	}

	@Test(expected = ServiceException.class)
	public void getClassMappingWithOnlyInheritedStatusCodeTest() {
		try {
			interceptedClassInstance.getIndexOutOfBoundsClassMappedException();
		} catch (Throwable e) {
			assertTrue(ServiceException.class.isAssignableFrom(e.getClass()));

			ServiceException exception = (ServiceException) e;

			assertEquals(CLASS_SERVICE_EXCEPTION_MESSAGE, exception.getMessage());
			assertEquals(HttpStatusCodes.STATUS_CODE_SERVER_ERROR, exception.getStatusCode());

			throw e;
		}
	}

	@Test(expected = ClassCastException.class)
	public void getUnmappedExceptionTest() {
		try {
			interceptedClassInstance.getunmappedException();
		} catch (Throwable e) {
			assertFalse(ServiceException.class.isAssignableFrom(e.getClass()));
			throw e;
		}
	}

	@Test(expected = NullPointerException.class)
	public void getChildClassOverridenMethodTest() {
		childInterceptedClassInstance.getNullPointerOverridenException();
	}

	@Override
	protected void configure() {
		MethodInterceptor exceptionMapperInterceptor = new ExceptionMapperInterceptor();

		bindInterceptor(com.google.inject.matcher.Matchers.subclassesOf(InterceptedClass.class),
				com.google.inject.matcher.Matchers.any(), exceptionMapperInterceptor);

		bind(InterceptedClass.class).in(Scopes.SINGLETON);
		bind(ChildInterceptedClass.class).in(Scopes.SINGLETON);
	}

	@ExceptionMapping(from = NullPointerException.class, to = ServiceException.class, message = CLASS_SERVICE_EXCEPTION_MESSAGE, statusCode = HttpStatusCodes.STATUS_CODE_CONFLICT)
	@ExceptionMapping(from = IllegalArgumentException.class, to = ServiceException.class, statusCode = HttpStatusCodes.STATUS_CODE_BAD_REQUEST)
	@ExceptionMapping(from = IndexOutOfBoundsException.class, to = ServiceException.class, message = CLASS_SERVICE_EXCEPTION_MESSAGE)
	static class InterceptedClass {

		@ExceptionMapping(from = NullPointerException.class, to = BadRequestException.class)
		public Boolean getNullPointerOverridenException() {
			throw new NullPointerException(METHOD_SERVICE_EXCEPTION_MESSAGE);
		}

		public Boolean getNullPointerClassMappedException() {
			throw new NullPointerException(METHOD_SERVICE_EXCEPTION_MESSAGE);
		}

		public Boolean getIllegalArgumentClassMappedException() {
			throw new IllegalArgumentException(METHOD_SERVICE_EXCEPTION_MESSAGE);
		}

		public Boolean getIndexOutOfBoundsClassMappedException() {
			throw new IndexOutOfBoundsException(METHOD_SERVICE_EXCEPTION_MESSAGE);
		}

		public Boolean getunmappedException() {
			throw new ClassCastException(METHOD_SERVICE_EXCEPTION_MESSAGE);
		}
	}

	static class ChildInterceptedClass extends InterceptedClass {
		@Override
		public Boolean getNullPointerOverridenException() {
			throw new NullPointerException(METHOD_SERVICE_EXCEPTION_MESSAGE);
		}
	}
}
