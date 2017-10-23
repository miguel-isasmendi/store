package com.store.domain.service.registration;

import com.store.domain.model.user.data.UserData;
import com.store.domain.model.validationCode.data.VerificationCodeData;

public interface UserRegistrationService {

	public void deletePendingValidationCodesFor(Long userId);

	public VerificationCodeData getLastPendingVerificationCode(Long userId);

	public VerificationCodeData createPendingVerificationCode(UserData user);
}
