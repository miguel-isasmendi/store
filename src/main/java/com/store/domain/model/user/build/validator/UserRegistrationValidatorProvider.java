package com.store.domain.model.user.build.validator;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;

import com.store.architecture.validator.ObjectBuildConversionOverseer;
import com.store.domain.architecture.validator.PasswordValidator;
import com.store.domain.exception.ErrorConstants;
import com.store.domain.model.user.data.UserRegistrationData;
import com.store.domain.model.user.dto.UserCreationDto;

public class UserRegistrationValidatorProvider {

	public static void validateDtoToDataTranslation(
			final ObjectBuildConversionOverseer<UserCreationDto, UserRegistrationData> overseer) {
		UserCreationDto userRegistration = overseer.getInputObject();

		overseer.checkArgument(StringUtils.isNotBlank(userRegistration.getFirstName()), "firstName es nulo o vacio");
		overseer.checkArgument(StringUtils.isAlphanumericSpace(userRegistration.getFirstName()),
				"name:[" + userRegistration.getFirstName() + "] no es alfanumerico");

		overseer.checkArgument(StringUtils.isNotBlank(userRegistration.getLastName()), "lastName es nulo o vacio");
		overseer.checkArgument(StringUtils.isAlphanumericSpace(userRegistration.getLastName()),
				"name:[" + userRegistration.getLastName() + "] no es alfanumerico");

		overseer.checkArgument(PasswordValidator.getValidator().validate(userRegistration.getPassword()),
				ErrorConstants.PASSWORD_FORMAT_ERROR);

		overseer.checkArgument(userRegistration.getEmail() != null, "email es nulo");
		overseer.checkArgument(EmailValidator.getInstance().isValid(userRegistration.getEmail()),
				ErrorConstants.EMAIL_FORMAT_ERROR);
	}

}
