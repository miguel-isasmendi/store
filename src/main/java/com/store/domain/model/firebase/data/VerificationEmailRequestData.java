package com.store.domain.model.firebase.data;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class VerificationEmailRequestData {
	@NonNull
	private String requestType;
	@NonNull
	private String idToken;
}