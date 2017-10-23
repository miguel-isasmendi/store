package com.store.domain.model.validationCode.build.coordinator;

import com.store.architecture.utils.DateUtils;
import com.store.domain.model.validationCode.VerificationCode;
import com.store.domain.model.validationCode.data.VerificationCodeData;

public class VerificationCodeBuildCoordinator {

	public static VerificationCodeData toData(VerificationCode verificationCode) {
		return VerificationCodeData.builder().code(verificationCode.getCode())
				.validUntilDate(DateUtils.dateFrom(verificationCode.getValidUntilDate()))
				.userId(verificationCode.getUserId()).build();
	}

}
