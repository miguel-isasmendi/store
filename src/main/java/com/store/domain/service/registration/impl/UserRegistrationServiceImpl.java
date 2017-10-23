package com.store.domain.service.registration.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;
import java.util.logging.Logger;

import org.apache.commons.lang.RandomStringUtils;

import com.google.inject.Inject;
import com.store.architecture.utils.DateUtils;
import com.store.domain.dao.registration.UserPendingValidationCodeDao;
import com.store.domain.model.user.data.UserData;
import com.store.domain.model.validationCode.build.coordinator.VerificationCodeBuildCoordinator;
import com.store.domain.model.validationCode.data.VerificationCodeCreationData;
import com.store.domain.model.validationCode.data.VerificationCodeData;
import com.store.domain.service.registration.UserRegistrationService;

import lombok.NonNull;

public class UserRegistrationServiceImpl implements UserRegistrationService {
	private static final Logger logger = Logger.getLogger(UserRegistrationServiceImpl.class.getName());

	private UserPendingValidationCodeDao registrationDao;

	@Inject
	public UserRegistrationServiceImpl(@NonNull UserPendingValidationCodeDao registrationDao) {
		this.registrationDao = registrationDao;
	}

	@Override
	public VerificationCodeData createPendingVerificationCode(@NonNull UserData user) {
		VerificationCodeData verificationCode = null;

		String registrationCode = RandomStringUtils.randomAlphanumeric(4).toUpperCase();
		logger.info("Created RegistrationCode = " + registrationCode);

		VerificationCodeCreationData verificationCodeData = VerificationCodeCreationData.builder()
				.code(registrationCode).validUntilDate(DateUtils.addHours(new Date(), 1)).userId(user.getUserId())
				.build();

		try {
			verificationCode = VerificationCodeBuildCoordinator
					.toData(registrationDao.createVerificationCode(verificationCodeData));
			logger.info("Created VerificationCode validUntilDate " + verificationCode.getValidUntilDate());

			return verificationCode;
		} catch (RuntimeException e) {
			Writer buffer = new StringWriter();
			PrintWriter pw = new PrintWriter(buffer);
			e.printStackTrace(pw);

			logger.severe(buffer.toString());

			if (verificationCode != null) {
				deletePendingValidationCodesFor(verificationCode.getUserId());
				logger.severe(String.format("deleted all validationCodes generated for userId: %s",
						verificationCode.getUserId()));
			}

			throw e;
		}

	}

	@Override
	public VerificationCodeData getLastPendingVerificationCode(@NonNull Long userId) {
		return VerificationCodeBuildCoordinator.toData(registrationDao.getLastVerificationCode(userId));
	}

	@Override
	public void deletePendingValidationCodesFor(@NonNull Long userId) {
		registrationDao.deleteValidationCodesFor(userId);
	}
}
