package com.store.domain.model.client;

import java.io.Serializable;

import com.google.cloud.Timestamp;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class Client implements Serializable {
	private static final long serialVersionUID = 1L;

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
	private Timestamp createdOn;
}
