package com.store.domain.dao.registration;

import com.store.domain.model.validationCode.VerificationCode;
import com.store.domain.model.validationCode.data.VerificationCodeCreationData;

import lombok.NonNull;

public interface UserPendingValidationCodeDao {

	public VerificationCode createVerificationCode(@NonNull VerificationCodeCreationData verificationCodeCreationData);

	public VerificationCode getLastVerificationCode(@NonNull Long userId);

	public void deleteValidationCodesFor(Long userId);
}
