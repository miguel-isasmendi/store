package com.store.domain.model.validationCode.data;

import java.util.Date;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class VerificationCodeCreationData {
	@NonNull
	private Long userId;
	@NonNull
	private String code;
	@NonNull
	private Date validUntilDate;
}
