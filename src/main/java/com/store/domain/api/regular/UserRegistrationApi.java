package com.store.domain.api.regular;

import static com.google.api.server.spi.config.ApiMethod.HttpMethod.POST;
import static com.google.api.server.spi.config.ApiMethod.HttpMethod.PUT;

import com.google.api.server.spi.ServiceException;
import com.google.api.server.spi.auth.common.User;
import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.ConflictException;
import com.google.inject.Inject;
import com.store.architecture.exception.annotation.ExceptionMapping;
import com.store.architecture.exception.dao.NotFoundDaoException;
import com.store.architecture.exception.service.ConflictServiceException;
import com.store.architecture.exception.service.InvalidArgumentsServiceException;
import com.store.architecture.exception.validation.RequestBusinessValidationException;
import com.store.architecture.exception.validation.UnexpectedBuildException;
import com.store.architecture.exception.validation.UnexpectedValidationException;
import com.store.domain.architecture.api.regular.FirebaseRegularUserAuthenticationProtectedApi;
import com.store.domain.model.user.data.UserData;
import com.store.domain.model.validationCode.dto.VerificationCodeAcceptanceDto;
import com.store.domain.service.registration.UserRegistrationCoordinatorService;
import com.store.domain.service.user.UserService;

import lombok.NonNull;

@ExceptionMapping(from = ConflictServiceException.class, to = ConflictException.class)
@ExceptionMapping(from = RequestBusinessValidationException.class, to = BadRequestException.class)
@ExceptionMapping(from = UnexpectedValidationException.class, to = BadRequestException.class)
@ExceptionMapping(from = UnexpectedBuildException.class, to = BadRequestException.class)
@ExceptionMapping(from = NotFoundDaoException.class, to = BadRequestException.class)
public class UserRegistrationApi extends FirebaseRegularUserAuthenticationProtectedApi {

	private UserRegistrationCoordinatorService userRegistrationService;
	private UserService userService;

	@Inject
	public UserRegistrationApi(@NonNull UserRegistrationCoordinatorService userRegistrationService,
			@NonNull UserService userService) {
		this.userRegistrationService = userRegistrationService;
		this.userService = userService;
	}

	@ApiMethod(httpMethod = POST, path = "/store/users/me/verification_code", apiKeyRequired = AnnotationBoolean.TRUE)
	@ExceptionMapping(from = InvalidArgumentsServiceException.class, to = ConflictException.class)
	public void sendVerificationCode(@NonNull User firebaseUser) throws ServiceException {
		UserData storeUser = userService.getByFirebaseId(firebaseUser.getId());

		userRegistrationService.sendVerificationCode(storeUser);
	}

	@ApiMethod(httpMethod = PUT, path = "/store/users/me/verification_code/acceptance", apiKeyRequired = AnnotationBoolean.TRUE)
	@ExceptionMapping(from = InvalidArgumentsServiceException.class, to = BadRequestException.class)
	public void receiveVerificationCode(@NonNull User firebaseUser,
			@NonNull VerificationCodeAcceptanceDto verificationCodeAcceptance) throws ServiceException {
		UserData storeUser = userService.getByFirebaseId(firebaseUser.getId());

		userRegistrationService.processVerificationCodeAcceptance(storeUser, verificationCodeAcceptance);
	}
}