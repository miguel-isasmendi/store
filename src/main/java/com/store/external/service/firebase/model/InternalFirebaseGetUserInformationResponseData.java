package com.store.external.service.firebase.model;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Getter
@NoArgsConstructor
public class InternalFirebaseGetUserInformationResponseData {
	@NonNull
	private List<InternalFirebaseUser> users;
}
