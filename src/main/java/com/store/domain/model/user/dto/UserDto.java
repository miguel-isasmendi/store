package com.store.domain.model.user.dto;

import java.util.Date;

import com.store.domain.model.user.UserStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
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
	private Date createdOn;
}