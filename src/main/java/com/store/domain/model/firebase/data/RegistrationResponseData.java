package com.store.domain.model.firebase.data;

import lombok.Getter;

@Getter
public class RegistrationResponseData {
	private String kind;
	private String idToken;
	private String localId;
	private String refreshToken;
	private String expiresIn;
}