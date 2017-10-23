package com.store.domain.model.client.data;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class ClientModificationData {
	@NonNull
	private String firstName;
	@NonNull
	private String lastName;
	@NonNull
	private String email;
}
