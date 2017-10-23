package com.store.domain.service.firebase;

import com.google.api.server.spi.ServiceException;
import com.store.domain.model.firebase.data.DeletionRequestData;
import com.store.domain.model.firebase.data.RegistrationRequestData;
import com.store.domain.model.firebase.data.RegistrationResponseData;
import com.store.domain.model.firebase.data.UserInformationRequestData;
import com.store.domain.model.firebase.data.UserInformationResponseData;

public interface FirebaseService {

	public RegistrationResponseData registerUser(RegistrationRequestData firebaseUserRegistrationData);

	public void deleteUser(DeletionRequestData firebaseUseDeletionRequestData);

	public UserInformationResponseData getFirebaseUserData(UserInformationRequestData firebaseGetUserInformationData) throws ServiceException;
}
