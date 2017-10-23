package com.store.domain.model.firebase.data;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class UserInformationRequestData {
	@NonNull
	private String idToken;
}