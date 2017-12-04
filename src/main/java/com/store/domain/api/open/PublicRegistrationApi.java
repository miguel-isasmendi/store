package com.store.domain.api.open;

import static com.google.api.server.spi.config.ApiMethod.HttpMethod.POST;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.ConflictException;
import com.google.inject.Inject;
import com.store.architecture.exception.annotation.ExceptionMapping;
import com.store.architecture.exception.annotation.ExceptionMappings;
import com.store.architecture.exception.dao.NotFoundDaoException;
import com.store.architecture.exception.service.ConflictServiceException;
import com.store.architecture.exception.validation.RequestBusinessValidationException;
import com.store.architecture.validator.ObjectBuildConversionOverseer;
import com.store.domain.model.user.build.coordinator.UserRegistrationBuildCoordinator;
import com.store.domain.model.user.build.validator.UserRegistrationValidatorProvider;
import com.store.domain.model.user.data.UserRegistrationData;
import com.store.domain.model.user.dto.UserCreationDto;
import com.store.domain.service.registration.UserRegistrationCoordinatorService;

import lombok.NonNull;

@Api(name = "registrationApi", version = "v1", namespace = @ApiNamespace(ownerDomain = "negrolas.com", ownerName = "negrolas.com", packagePath = ""))
@ExceptionMappings(value = { @ExceptionMapping(from = ConflictServiceException.class, to = ConflictException.class),
		@ExceptionMapping(from = RequestBusinessValidationException.class, to = BadRequestException.class),
		@ExceptionMapping(from = NotFoundDaoException.class, to = BadRequestException.class) })
public class PublicRegistrationApi {

	private UserRegistrationCoordinatorService registrationService;

	@Inject
	public PublicRegistrationApi(@NonNull UserRegistrationCoordinatorService userRegistrationService) {
		this.registrationService = userRegistrationService;
	}

	@ApiMethod(httpMethod = POST, path = "/store/users/registrations")
	public void register(@NonNull UserCreationDto userRegistrationDto) throws Exception {

		UserRegistrationData userRegistrationData = ObjectBuildConversionOverseer
				.<UserCreationDto, UserRegistrationData>builder().inputObject(userRegistrationDto)
				.preBuildValidator(UserRegistrationValidatorProvider::validateDtoToDataTranslation)
				.buildClosure(UserRegistrationBuildCoordinator::buildToData).build().execute();

		registrationService.registerUser(userRegistrationData);
	}
}