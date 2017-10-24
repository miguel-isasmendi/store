package com.store.domain.model.client.build.validator;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;

import com.store.architecture.validator.ObjectBuildConversionOverseer;
import com.store.domain.exception.ErrorConstants;
import com.store.domain.model.client.data.ClientCreationData;
import com.store.domain.model.client.dto.ClientCreationDto;

public class ClientCreationValidatorProvider {

	public static void validateDtoToDataTranslation(
			ObjectBuildConversionOverseer<ClientCreationDto, ClientCreationData> overseer) {
		ClientCreationDto clientDto = overseer.getInputObject();

		overseer.checkArgument(StringUtils.isNotEmpty(clientDto.getFirstName()),
				ErrorConstants.formatError(ErrorConstants.ATTRIBUTE_SHOULD_NOT_BE_EMPTY, "firstName"));
		overseer.checkArgument(StringUtils.isNotEmpty(clientDto.getLastName()),
				ErrorConstants.formatError(ErrorConstants.ATTRIBUTE_SHOULD_NOT_BE_EMPTY, "lastName"));
		overseer.checkArgument(EmailValidator.getInstance().isValid(clientDto.getEmail()),
				ErrorConstants.formatError(ErrorConstants.EMAIL_FORMAT_ERROR, "email"));
	}
}
