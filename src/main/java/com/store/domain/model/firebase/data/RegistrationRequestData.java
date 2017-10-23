package com.store.domain.model.firebase.data;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class RegistrationRequestData {
	@NonNull
	private String password;
	@NonNull
	private String email;
}