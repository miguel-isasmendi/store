package com.store.external.service.firebase.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Getter
@NoArgsConstructor
public class InternalFirebaseUser {
	@NonNull
	private Boolean emailVerified;
}
