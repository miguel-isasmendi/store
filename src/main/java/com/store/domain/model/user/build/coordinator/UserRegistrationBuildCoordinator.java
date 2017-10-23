package com.store.domain.model.user.build.coordinator;

import com.store.architecture.validator.ObjectBuildConversionOverseer;
import com.store.domain.model.user.data.UserRegistrationData;
import com.store.domain.model.user.dto.UserCreationDto;

public class UserRegistrationBuildCoordinator {
	public static UserRegistrationData toData(UserCreationDto userRegistration) {
		return UserRegistrationData.builder().email(userRegistration.getEmail())
				.firstName(userRegistration.getFirstName()).lastName(userRegistration.getLastName())
				.password(userRegistration.getPassword()).build();
	}

	public static UserRegistrationData buildToData(
			ObjectBuildConversionOverseer<UserCreationDto, UserRegistrationData> overseer) {
		return toData(overseer.getInputObject());
	}
}
