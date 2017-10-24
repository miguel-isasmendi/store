package com.store.domain.model.client.data;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class ClientModificationData {
	@NonNull
	private Long clientId;
	private String firstName;
	private String lastName;
	private String email;
}
