package com.store.domain.model.user.build.coordinator;

import com.store.architecture.utils.DateUtils;
import com.store.domain.model.user.User;
import com.store.domain.model.user.data.UserCreationData;
import com.store.domain.model.user.data.UserData;
import com.store.domain.model.user.data.UserRegistrationData;
import com.store.domain.model.user.dto.UserDto;

public class UserBuildCoordinator {
	public static UserCreationData toData(UserRegistrationData userRegistration, String firebaseId) {
		return UserCreationData.builder().email(userRegistration.getEmail()).firstName(userRegistration.getFirstName())
				.firebaseId(firebaseId).lastName(userRegistration.getLastName()).build();
	}

	public static UserDto toDto(UserData user) {
		return UserDto.builder().userId(user.getUserId()).status(user.getStatus()).email(user.getEmail())
				.firstName(user.getFirstName()).lastName(user.getLastName()).createdOn(user.getCreatedOn()).build();
	}

	public static UserData toData(User user) {
		return UserData.builder().userId(user.getUserId()).status(user.getStatus()).email(user.getEmail())
				.firstName(user.getFirstName()).lastName(user.getLastName()).firebaseId(user.getFirebaseId())
				.createdOn(DateUtils.dateFrom(user.getCreatedOn())).build();
	}
}
