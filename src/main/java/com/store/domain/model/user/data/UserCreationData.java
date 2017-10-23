package com.store.domain.model.user.data;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class UserCreationData {
	@NonNull
	private String firstName;
	@NonNull
	private String lastName;
	@NonNull
	private String firebaseId;
	@NonNull
	private String email;
}