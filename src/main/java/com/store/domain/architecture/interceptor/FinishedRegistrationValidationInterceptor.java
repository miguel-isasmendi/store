package com.store.domain.architecture.interceptor;

import java.util.logging.Logger;

import javax.servlet.ServletException;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import com.google.api.server.spi.auth.common.User;
import com.google.api.server.spi.response.ConflictException;
import com.store.architecture.exception.dao.NotFoundDaoException;
import com.store.architecture.exception.service.ConflictServiceException;
import com.store.architecture.listener.SystemServletConfigListener;
import com.store.domain.exception.ErrorConstants;
import com.store.domain.model.user.UserStatus;
import com.store.domain.model.user.data.UserData;
import com.store.domain.service.registration.UserRegistrationService;
import com.store.domain.service.user.UserService;

/**
 * <p>
 * This method intercepter searches for the user's accepted privacy terms and
 * interrupts the access to the functionality if there exists Terms and
 * Conditions with a mayor version change in the database returning a conflict
 * exception.
 * </p>
 */
public class FinishedRegistrationValidationInterceptor implements MethodInterceptor {
	private static final Logger logger = Logger.getLogger(FinishedRegistrationValidationInterceptor.class.getName());

	private UserService userService;
	private UserRegistrationService userRegistrationService;

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		if (invocation.getArguments().length != 0
				&& User.class.isAssignableFrom(invocation.getArguments()[0].getClass())) {

			User firebaseUser = (User) invocation.getArguments()[0];
			UserData storeUser = getUserService().getByFirebaseId(firebaseUser.getId());

			if (UserStatus.NEW.equals(storeUser.getStatus())) {
				try {
					getUserRegistrationService().getLastPendingVerificationCode(storeUser.getUserId());

					logger.info(
							String.format("Found that the user (id = %s) has a pending acceptance verification code",
									storeUser.getUserId()));
					// TODO change this exception upon exception refactoring
					throw new ServletException(new ConflictException(new ConflictServiceException(
							ErrorConstants.THE_USER_HAS_A_PENDING_ACCEPTANCE_VERIFICATION_CODE)));

				} catch (NotFoundDaoException exception) {
					// We should proceed working as if there where not need to check any
					// verification code.
				}
			}
		}

		return invocation.proceed();
	}

	public synchronized UserService getUserService() {
		if (userService == null) {
			userService = SystemServletConfigListener.getCreatedInjector().getInstance(UserService.class);
		}
		return userService;
	}

	public synchronized UserRegistrationService getUserRegistrationService() {
		if (userRegistrationService == null) {
			userRegistrationService = SystemServletConfigListener.getCreatedInjector()
					.getInstance(UserRegistrationService.class);
		}
		return userRegistrationService;
	}
}
