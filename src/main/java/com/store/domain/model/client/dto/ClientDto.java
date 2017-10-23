package com.store.domain.model.client.dto;

import java.util.Date;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class ClientDto {
	@NonNull
	private Long clientId;
	@NonNull
	private String firstName;
	@NonNull
	private String lastName;
	@NonNull
	private String email;
	@NonNull
	private Long createdByUserId;
	@NonNull
	private Date createdOn;
}
