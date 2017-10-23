package com.store.domain.model.user.data;

import java.util.Date;

import com.store.domain.model.user.UserStatus;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class UserData {
	@NonNull
	private Long userId;
	@NonNull
	private String firebaseId;
	@NonNull
	private UserStatus status;
	@NonNull
	private String firstName;
	@NonNull
	private String lastName;
	@NonNull
	private String email;
	@NonNull
	private Date createdOn;
}