package com.store.domain.service.registration;

import com.store.domain.model.user.data.UserData;
import com.store.domain.model.user.data.UserRegistrationData;
import com.store.domain.model.validationCode.dto.VerificationCodeAcceptanceDto;

public interface UserRegistrationCoordinatorService {

	public void registerUser(UserRegistrationData userRegistrationData);

	public void processVerificationCodeAcceptance(UserData storeUser,
			VerificationCodeAcceptanceDto verificationCodeAcceptance);

	public void sendVerificationCode(UserData storeUser);

}