package com.store.domain.model.client.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class ClientModificationDto {
	@NonNull
	private String firstName;
	@NonNull
	private String lastName;
	@NonNull
	private String email;
}
