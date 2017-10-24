package com.store.domain.model.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientModificationDto {
	private String firstName;
	private String lastName;
	private String email;
}
