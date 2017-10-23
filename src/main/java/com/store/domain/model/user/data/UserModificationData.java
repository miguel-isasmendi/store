package com.store.domain.model.user.data;

import com.store.domain.model.user.UserStatus;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class UserModificationData {
	@NonNull
	private Long userId;
	private UserStatus status;
	private String firstName;
	private String lastName;
	private String email;
}