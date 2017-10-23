package com.store.domain.model.firebase.build.validator;

import com.store.architecture.validator.ObjectBuildConversionOverseer;
import com.store.domain.architecture.validator.EmailValidator;
import com.store.domain.architecture.validator.PasswordValidator;
import com.store.domain.exception.ErrorConstants;
import com.store.domain.model.firebase.data.RegistrationRequestData;

public class FirebaseUserRegistrationRequestValidatorProvider {
	public static void validateDtoToDataTranslation(
			ObjectBuildConversionOverseer<RegistrationRequestData, RegistrationRequestData> overseer) {
		RegistrationRequestData requestData = overseer.getInputObject();

		overseer.checkArgument(PasswordValidator.getValidator().validate(requestData.getPassword()),
				ErrorConstants.PASSWORD_FORMAT_ERROR);

		overseer.checkArgument(EmailValidator.validate(requestData.getEmail()), ErrorConstants.EMAIL_FORMAT_ERROR);
	}

}
