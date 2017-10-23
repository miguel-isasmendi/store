package com.store.domain.service.user.impl;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.inject.Inject;
import com.store.architecture.cache.CacheHandler;
import com.store.architecture.exception.dao.NotFoundDaoException;
import com.store.architecture.exception.service.ConflictServiceException;
import com.store.domain.dao.user.UserDao;
import com.store.domain.exception.ErrorConstants;
import com.store.domain.model.user.User;
import com.store.domain.model.user.build.coordinator.UserBuildCoordinator;
import com.store.domain.model.user.data.UserCreationData;
import com.store.domain.model.user.data.UserData;
import com.store.domain.model.user.data.UserModificationData;
import com.store.domain.service.user.UserService;

import lombok.NonNull;

/**
 * Implementation for UserService, caching the results.
 */
public class CachedUserService implements UserService {
	private static final String CACHE_PREFIX = "user_";
	private static final String CACHE_FIREBASE_ID_PREFIX = CACHE_PREFIX + "firebase_id_";

	private UserDao userDao;
	private CacheHandler<User> userCacheHandler;
	private CacheHandler<User> userByFirebaseIdCacheHandler;

	@Inject
	public CachedUserService(UserDao userDao, MemcacheService cache) {
		this.userDao = userDao;

		this.userCacheHandler = CacheHandler.<User>builder().cache(cache)
				.keyGeneratorClosure(element -> element.getUserId()).prefix(CACHE_PREFIX).build();

		this.userByFirebaseIdCacheHandler = CacheHandler.<User>builder().cache(cache)
				.keyGeneratorClosure(element -> element.getFirebaseId()).prefix(CACHE_FIREBASE_ID_PREFIX).build();
	}

	@Override
	public UserData create(@NonNull UserCreationData userCreationData) {

		try {
			getByFirebaseId(userCreationData.getFirebaseId());
			throw new ConflictServiceException(
					ErrorConstants.formatError(ErrorConstants.ELEMENT_ALREADY_EXISTS, "un usuario"));
		} catch (NotFoundDaoException exception) {
			User user = userDao.create(userCreationData);

			userCacheHandler.putIntoCache(user);
			userByFirebaseIdCacheHandler.putIntoCache(user);

			return UserBuildCoordinator.toData(user);
		}

	}

	@Override
	public UserData getByFirebaseId(@NonNull String firebaseId) {

		User user = userByFirebaseIdCacheHandler.getFromCacheUsingPartialKey(firebaseId);
		if (user != null) {
			return UserBuildCoordinator.toData(user);
		} else {
			user = userDao.getByfirebaseId(firebaseId);
			userCacheHandler.putIntoCache(user);
			userByFirebaseIdCacheHandler.putIntoCache(user);
		}
		return UserBuildCoordinator.toData(user);
	}

	@Override
	public UserData update(@NonNull UserModificationData userModificationData) {

		User user = userDao.update(userModificationData);

		userCacheHandler.putIntoCache(user);
		userByFirebaseIdCacheHandler.putIntoCache(user);
		return UserBuildCoordinator.toData(user);
	}

	@Override
	public UserData getById(@NonNull Long userId) {
		User user = userCacheHandler.getFromCacheUsingPartialKey(userId);

		if (user == null) {
			user = userDao.getUserById(userId);

			userCacheHandler.putIntoCache(user);
			userByFirebaseIdCacheHandler.putIntoCache(user);
		}

		return UserBuildCoordinator.toData(user);
	}

	@Override
	public void delete(Long userId) {
		UserData user = getById(userId);

		userCacheHandler.deleteFromCacheUsingPartialKey(user.getUserId());
		userByFirebaseIdCacheHandler.deleteFromCacheUsingPartialKey(user.getFirebaseId());

		userDao.delete(user.getUserId());
	}

}