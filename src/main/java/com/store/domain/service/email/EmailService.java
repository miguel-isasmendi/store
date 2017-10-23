package com.store.domain.service.email;

import com.store.domain.model.user.data.UserData;

public interface EmailService {

	public void sendVerificationCodeEmail(UserData userData, String registrationCode);

	public void sendWelcomeEmail(UserData userData);

}
