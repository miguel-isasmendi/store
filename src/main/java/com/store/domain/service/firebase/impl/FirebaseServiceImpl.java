package com.store.domain.service.firebase.impl;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.inject.Named;

import org.apache.http.HttpHeaders;

import com.google.api.server.spi.ServiceException;
import com.google.api.server.spi.response.InternalServerErrorException;
import com.google.appengine.api.urlfetch.HTTPHeader;
import com.google.appengine.api.urlfetch.HTTPMethod;
import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.common.net.MediaType;
import com.google.gson.Gson;
import com.google.inject.Inject;
import com.store.architecture.exception.service.InternalServiceErrorException;
import com.store.architecture.validator.ObjectBuildConversionOverseer;
import com.store.domain.model.firebase.build.validator.FirebaseUserRegistrationRequestValidatorProvider;
import com.store.domain.model.firebase.data.DeletionRequestData;
import com.store.domain.model.firebase.data.RegistrationRequestData;
import com.store.domain.model.firebase.data.RegistrationResponseData;
import com.store.domain.model.firebase.data.UserInformationRequestData;
import com.store.domain.model.firebase.data.UserInformationResponseData;
import com.store.domain.service.firebase.FirebaseService;
import com.store.external.service.firebase.model.InternalFirebaseGetUserInformationResponseData;
import com.store.external.service.firebase.model.InternalFirebaseUser;

import lombok.Getter;
import lombok.NonNull;

public class FirebaseServiceImpl implements FirebaseService {

	private static final Logger logger = Logger.getLogger(FirebaseServiceImpl.class.getName());

	private URLFetchService requestService;
	private Gson gson;

	private String registerUserUrl;
	private String deleteUserUrl;
	private String getUserDataUrl;
	private String firebaseKey;
	private String firebaseRootUrl;

	@Inject
	public FirebaseServiceImpl(@NonNull URLFetchService requestService, @NonNull Gson gson,
			@NonNull @Named("firebase.api.key") String firebaseKey,
			@NonNull @Named("firebase.base.url") String firebaseRootUrl,
			@NonNull @Named("firebase.registration.url") String registerUserUrl,
			@NonNull @Named("firebase.deleteUser.url") String deleteUserUrl,
			@NonNull @Named("firebase.getUserData.url") String getUserDataUrl) {

		this.requestService = requestService;
		this.gson = gson;
		this.registerUserUrl = registerUserUrl;
		this.deleteUserUrl = deleteUserUrl;
		this.getUserDataUrl = getUserDataUrl;
		this.firebaseRootUrl = firebaseRootUrl;
		this.firebaseKey = firebaseKey;
	}

	@Override
	@SuppressWarnings("unchecked")
	public RegistrationResponseData registerUser(@NonNull RegistrationRequestData firebaseUserRegistrationData) {

		ObjectBuildConversionOverseer.<RegistrationRequestData, RegistrationRequestData>builder()
				.inputObject(firebaseUserRegistrationData)
				.preBuildValidator(FirebaseUserRegistrationRequestValidatorProvider::validateDtoToDataTranslation)
				.buildClosure(ObjectBuildConversionOverseer.NO_OP_BUILD_CLOSURE).build().execute();

		return gson.fromJson(executeRequest(registerUserUrl, firebaseUserRegistrationData),
				RegistrationResponseData.class);
	}

	private String executeRequest(@NonNull String url, @NonNull Object requestBody) {
		return executeRequest(url, requestBody, new HashMap<Integer, String>());
	}

	private String executeRequest(@NonNull String url, @NonNull Object requestBody,
			@NonNull Map<Integer, String> errorMessages) {
		String requestBodyString = gson.toJson(requestBody);

		HTTPRequest request;
		try {
			String urlToRequest = this.firebaseRootUrl + url + "?key=" + this.firebaseKey;
			request = new HTTPRequest(new URL(urlToRequest), HTTPMethod.POST);
			request.setPayload(
					requestBodyString.getBytes(StandardCharsets.UTF_8.name().replaceFirst("-", "").toLowerCase()));
			request.setHeader(new HTTPHeader(HttpHeaders.CONTENT_TYPE, MediaType.JSON_UTF_8.toString()));

			HTTPResponse response = requestService.fetch(request);

			if (response.getResponseCode() >= 400) {

				errorMessages.put(404, "Could not reach firebase service at: " + firebaseRootUrl + url);

				String errorString = errorMessages.get(response.getResponseCode());

				String bodyString = new String(response.getContent()).replaceAll("\n", "");
				String errorMessage = "Problem registering user in firebase!!!";

				if (errorString == null) {
					errorString = "There has been an internal server error!!!";
					errorMessage = bodyString;
				}

				handleBadResponse(response.getResponseCode(), gson.fromJson(bodyString, FirebaseErrorResponse.class),
						errorMessage);
			}

			return new String(response.getContent());
		} catch (IOException e) {
			throw new InternalServiceErrorException(e);
		}

	}

	@Override
	public UserInformationResponseData getFirebaseUserData(
			@NonNull UserInformationRequestData firebaseGetUserInformationData) throws ServiceException {

		String responseContent = executeRequest(getUserDataUrl, firebaseGetUserInformationData);

		InternalFirebaseGetUserInformationResponseData internalFirebaseGetUserInformationResponseData = gson
				.fromJson(responseContent, InternalFirebaseGetUserInformationResponseData.class);

		if (internalFirebaseGetUserInformationResponseData.getUsers().size() != 1) {
			logger.severe(
					"Problem requesting user information in firebase, multiple users returned:" + responseContent);
			throw new InternalServerErrorException(responseContent);
		}

		InternalFirebaseUser internalFirebaseUser = internalFirebaseGetUserInformationResponseData.getUsers().get(0);

		return UserInformationResponseData.builder().emailVerified(internalFirebaseUser.getEmailVerified()).build();
	}

	@Override
	public void deleteUser(@NonNull DeletionRequestData firebaseUseDeletionRequestData) {
		executeRequest(deleteUserUrl, firebaseUseDeletionRequestData);
	}

	private void handleBadResponse(@NonNull Integer responseCode, @NonNull FirebaseErrorResponse firebaseError,
			@NonNull String errorMessage) {

		if ((400 <= responseCode || responseCode < 500) && firebaseError.getFirebaseError() != null) {
			String firebaseErrorMessage = firebaseError.getFirebaseError().getMessage();

			logger.warning(String.format(errorMessage + ", errorMessage: %s", firebaseErrorMessage));
			throw new IllegalArgumentException(errorMessage + ", errorMessage: " + firebaseErrorMessage);
		} else {
			logger.severe(errorMessage);
			throw new RuntimeException(errorMessage);
		}
	}

	@Getter
	public static class FirebaseErrorResponse {
		private FirebaseError firebaseError;

		@Getter
		public static class FirebaseError {
			private List<FirebaseErrorItem> errors;
			private String code;
			private String message;
		}

		@Getter
		public static class FirebaseErrorItem {
			private String domain;
			private String reason;
			private String message;
		}
	}
}