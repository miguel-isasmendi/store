package com.store.domain.api.admin;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.google.api.server.spi.ServiceException;
import com.google.api.server.spi.auth.common.User;
import com.google.api.server.spi.config.ApiMethod;
import com.google.inject.Inject;
import com.store.domain.architecture.api.admin.FirebaseAdminAuthenticationProtectedApi;
import com.store.domain.model.user.UserStatus;
import com.store.domain.model.user.data.UserData;
import com.store.domain.model.user.dto.UserDto;
import com.store.domain.service.email.EmailService;

import lombok.NonNull;

public class EmailSenderApi extends FirebaseAdminAuthenticationProtectedApi {
	private EmailService emailService;

	@Inject
	public EmailSenderApi(EmailService emailService) {
		this.emailService = emailService;
	}

	@ApiMethod(httpMethod = ApiMethod.HttpMethod.POST, path = "/admin/store/users/emails/verification-codes")
	public void sendVerificationCodeEmail(@NonNull User firebaseAdminUser, UserDto userDto) throws ServiceException {

		UserData userData = UserData.builder().userId(0l).status(UserStatus.NEW).email(userDto.getEmail())
				.firstName(userDto.getFirstName()).lastName(userDto.getLastName()).firebaseId(StringUtils.EMPTY)
				.createdOn(new Date()).build();

		emailService.sendVerificationCodeEmail(userData, "X2xP");

	}

	@ApiMethod(httpMethod = ApiMethod.HttpMethod.POST, path = "/admin/store/users/emails/welcome")
	public void sendWelcomeEmail(@NonNull User firebaseAdminUser, UserDto userDto) throws ServiceException {

		UserData userData = UserData.builder().userId(0l).status(UserStatus.NEW).email(userDto.getEmail())
				.firstName(userDto.getFirstName()).lastName(userDto.getLastName()).firebaseId(StringUtils.EMPTY)
				.createdOn(new Date()).build();

		emailService.sendWelcomeEmail(userData);
	}

}