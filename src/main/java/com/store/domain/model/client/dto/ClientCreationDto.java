package com.store.domain.model.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientCreationDto {
	@NonNull
	private String firstName;
	@NonNull
	private String lastName;
	@NonNull
	private String email;
}
