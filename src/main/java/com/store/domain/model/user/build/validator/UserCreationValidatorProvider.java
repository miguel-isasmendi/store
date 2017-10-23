package com.store.domain.model.user.build.validator;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;

import com.store.architecture.validator.ObjectBuildConversionOverseer;
import com.store.domain.architecture.validator.PasswordValidator;
import com.store.domain.exception.ErrorConstants;
import com.store.domain.model.user.data.UserCreationData;
import com.store.domain.model.user.data.UserRegistrationData;

public class UserCreationValidatorProvider {

	public static void validateDtoToDataTranslation(
			final ObjectBuildConversionOverseer<UserRegistrationData, UserCreationData> overseer) {
		UserRegistrationData userRegistration = overseer.getInputObject();

		overseer.checkArgument(StringUtils.isNotBlank(userRegistration.getFirstName()), "name is null, blank or empty");
		overseer.checkArgument(StringUtils.isAlphanumericSpace(userRegistration.getFirstName()),
				"name:[" + userRegistration.getFirstName() + "] is not alphanumeric");

		overseer.checkArgument(StringUtils.isNotBlank(userRegistration.getLastName()), "name is null, blank or empty");
		overseer.checkArgument(StringUtils.isAlphanumericSpace(userRegistration.getLastName()),
				"name:[" + userRegistration.getLastName() + "] is not alphanumeric");

		overseer.checkArgument(PasswordValidator.getValidator().validate(userRegistration.getPassword()),
				ErrorConstants.PASSWORD_FORMAT_ERROR);

		overseer.checkArgument(userRegistration.getEmail() != null, "email is null");
		overseer.checkArgument(EmailValidator.getInstance().isValid(userRegistration.getEmail()),
				ErrorConstants.EMAIL_FORMAT_ERROR);
	}

}
