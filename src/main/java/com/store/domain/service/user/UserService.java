package com.store.domain.service.user;

import com.store.domain.model.user.data.UserCreationData;
import com.store.domain.model.user.data.UserData;
import com.store.domain.model.user.data.UserModificationData;

public interface UserService {

	public UserData create(UserCreationData userRegistrationData);

	public UserData getByFirebaseId(String firebaseId);

	public UserData update(UserModificationData userModificationData);

	public void delete(Long userId);

	public UserData getById(Long userId);
}
