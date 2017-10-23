package com.store.domain.service.registration.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;

import com.google.inject.Inject;
import com.store.architecture.exception.service.ConflictServiceException;
import com.store.architecture.exception.service.InvalidArgumentsServiceException;
import com.store.architecture.validator.ObjectBuildConversionOverseer;
import com.store.domain.exception.ErrorConstants;
import com.store.domain.model.firebase.data.DeletionRequestData;
import com.store.domain.model.firebase.data.RegistrationRequestData;
import com.store.domain.model.firebase.data.RegistrationResponseData;
import com.store.domain.model.user.UserStatus;
import com.store.domain.model.user.build.coordinator.UserBuildCoordinator;
import com.store.domain.model.user.build.validator.UserCreationValidatorProvider;
import com.store.domain.model.user.data.UserCreationData;
import com.store.domain.model.user.data.UserData;
import com.store.domain.model.user.data.UserModificationData;
import com.store.domain.model.user.data.UserRegistrationData;
import com.store.domain.model.validationCode.data.VerificationCodeData;
import com.store.domain.model.validationCode.dto.VerificationCodeAcceptanceDto;
import com.store.domain.service.email.EmailService;
import com.store.domain.service.firebase.FirebaseService;
import com.store.domain.service.registration.UserRegistrationCoordinatorService;
import com.store.domain.service.registration.UserRegistrationService;
import com.store.domain.service.user.UserService;

import lombok.NonNull;

public class UserRegistrationCoordinatorServiceImpl implements UserRegistrationCoordinatorService {
	private static final Logger logger = Logger.getLogger(UserRegistrationCoordinatorServiceImpl.class.getName());

	private FirebaseService firebaseService;
	private UserService userService;
	private UserRegistrationService userRegistrationService;
	private EmailService emailService;

	@Inject
	public UserRegistrationCoordinatorServiceImpl(@NonNull FirebaseService firebaseService,
			@NonNull UserService userService, @NonNull EmailService emailService, @NonNull UserRegistrationService userRegistrationService) {
		this.firebaseService = firebaseService;
		this.userService = userService;
		this.emailService = emailService;
		this.userRegistrationService = userRegistrationService;
	}

	@Override
	public void registerUser(@NonNull UserRegistrationData userRegistrationData) {

		RegistrationResponseData firebaseUser = registerFirebaseUser(userRegistrationData);

		UserData user = null;
		try {
			user = createUser(userRegistrationData, firebaseUser);
			logger.info(String.format("Created user with userId: %s", user.getUserId()));

			createAndSendVerificationCodeToUser(user);

		} catch (RuntimeException e) {
			Writer buffer = new StringWriter();
			PrintWriter pw = new PrintWriter(buffer);
			e.printStackTrace(pw);

			logger.severe(buffer.toString());

			deleteFirebaseUser(firebaseUser);
			logger.severe(String.format("Deleted firebase user with firebaseId: %s, due to cause: %s",
					firebaseUser.getLocalId(), e));

			if (user != null) {
				userService.delete(user.getUserId());
				logger.severe(String.format("Deleted store user with userId: %s", user.getUserId()));
			}

			throw new ConflictServiceException("Unable to create user!");
		}
	}

	private void createAndSendVerificationCodeToUser(@NonNull UserData user) {
		VerificationCodeData verificationCodeSent = userRegistrationService.createPendingVerificationCode(user);
		logger.info("Verification code sent!");

		emailService.sendVerificationCodeEmail(user, verificationCodeSent.getCode());
	}

	private UserData createUser(@NonNull UserRegistrationData userRegistrationData,
			@NonNull final RegistrationResponseData firebaseUser) {

		UserCreationData userCreationData = ObjectBuildConversionOverseer
				.<UserRegistrationData, UserCreationData>builder().inputObject(userRegistrationData)
				.preBuildValidator(UserCreationValidatorProvider::validateDtoToDataTranslation)
				.buildClosure(
						overseer -> UserBuildCoordinator.toData(overseer.getInputObject(), firebaseUser.getLocalId()))
				.build().execute();

		return userService.create(userCreationData);
	}

	private RegistrationResponseData registerFirebaseUser(@NonNull UserRegistrationData userRegistrationData) {

		RegistrationRequestData firebaseUserRegistrationData = RegistrationRequestData.builder()
				.email(userRegistrationData.getEmail()).password(userRegistrationData.getPassword()).build();

		RegistrationResponseData firebaseUser = firebaseService.registerUser(firebaseUserRegistrationData);
		logger.info(String.format("created user in firebase with firebaseId: %s", firebaseUser.getLocalId()));

		return firebaseUser;
	}

	private void deleteFirebaseUser(@NonNull RegistrationResponseData firebaseUser) {
		DeletionRequestData firebaseUserDeletionRequestData = DeletionRequestData.builder()
				.idToken(firebaseUser.getIdToken()).build();
		firebaseService.deleteUser(firebaseUserDeletionRequestData);
	}

	@Override
	public void processVerificationCodeAcceptance(@NonNull UserData storeUser,
			@NonNull VerificationCodeAcceptanceDto verificationCodeAcceptance) {
		if (!UserStatus.NEW.equals(storeUser.getStatus())) {
			throw new ConflictServiceException(ErrorConstants.THE_USER_HAS_ALREADY_CONFIRMED_THE_MAIL);
		}

		VerificationCodeData lastVerificationCodeSent = userRegistrationService
				.getLastPendingVerificationCode(storeUser.getUserId());

		if (lastVerificationCodeSent.getValidUntilDate().before(new Date())) {
			throw new InvalidArgumentsServiceException(ErrorConstants.THE_USER_HAS_AN_EXPIRED_VERIFICATION_CODE);
		}

		if (StringUtils.equals(lastVerificationCodeSent.getCode(), verificationCodeAcceptance.getVerificationCode())) {
			userRegistrationService.deletePendingValidationCodesFor(storeUser.getUserId());
			userService.update(
					UserModificationData.builder().userId(storeUser.getUserId()).status(UserStatus.CONFIRMED).build());

			emailService.sendWelcomeEmail(storeUser);
		} else {
			throw new InvalidArgumentsServiceException(ErrorConstants.THE_VALIDATION_CODE_RECEIVED_IS_INVALID);
		}
	}

	@Override
	public void sendVerificationCode(@NonNull UserData storeUser) {
		if (!UserStatus.NEW.equals(storeUser.getStatus())) {
			throw new ConflictServiceException(ErrorConstants.THE_USER_HAS_ALREADY_CONFIRMED_THE_MAIL);
		}

		createAndSendVerificationCodeToUser(storeUser);
	}
}