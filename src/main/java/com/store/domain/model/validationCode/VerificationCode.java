package com.store.domain.model.validationCode;

import java.io.Serializable;

import com.google.cloud.Timestamp;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class VerificationCode implements Serializable {
	private static final long serialVersionUID = 1L;

	@NonNull
	private Long verificationCodeId;
	@NonNull
	private Long userId;
	@NonNull
	private String code;
	@NonNull
	private Timestamp validUntilDate;
	@NonNull
	private Timestamp createdOn;
}
