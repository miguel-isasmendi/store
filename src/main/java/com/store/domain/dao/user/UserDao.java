package com.store.domain.dao.user;

import com.store.domain.model.user.User;
import com.store.domain.model.user.data.UserCreationData;
import com.store.domain.model.user.data.UserModificationData;

public interface UserDao {

	public User create(UserCreationData data);

	public User getByfirebaseId(String firebaseId);

	public void delete(Long userId);

	public User update(UserModificationData newUserData);

	public User getUserById(Long userId);
}