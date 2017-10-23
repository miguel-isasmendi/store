package com.store.domain.model.user;

import java.io.Serializable;

import com.google.cloud.Timestamp;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class User implements Serializable {
	private static final long serialVersionUID = 1L;

	@NonNull
	private Long userId;
	@NonNull
	private UserStatus status;
	@NonNull
	private String firstName;
	@NonNull
	private String lastName;
	@NonNull
	private String email;
	@NonNull
	private String firebaseId;
	@NonNull
	private Timestamp createdOn;
}