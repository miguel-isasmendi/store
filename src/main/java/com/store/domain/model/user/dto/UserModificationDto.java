package com.store.domain.model.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class UserModificationDto {
	@NonNull
	private Long userId;
	private String firstName;
	private String lastName;
	private String email;
}