package com.store.domain.model.user.dto;

import lombok.Getter;

@Getter
public class UserCreationDto {
	private String firstName;
	private String lastName;
	private String email;
	private String password;
}