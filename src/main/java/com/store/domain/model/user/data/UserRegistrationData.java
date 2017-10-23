package com.store.domain.model.user.data;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class UserRegistrationData {
	@NonNull
	private String firstName;
	@NonNull
	private String lastName;
	@NonNull
	private String password;
	@NonNull
	private String email;
}