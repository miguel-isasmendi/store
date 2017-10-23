package com.store.domain.api.regular;

import com.google.api.server.spi.ServiceException;
import com.google.api.server.spi.auth.common.User;
import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.response.NotFoundException;
import com.google.inject.Inject;
import com.store.architecture.exception.annotation.ExceptionMapping;
import com.store.architecture.exception.dao.NotFoundDaoException;
import com.store.domain.architecture.api.regular.FirebaseRegularUserAuthenticationProtectedApi;
import com.store.domain.model.user.build.coordinator.UserBuildCoordinator;
import com.store.domain.model.user.dto.UserDto;
import com.store.domain.service.user.UserService;

import lombok.NonNull;

@ExceptionMapping(from = NotFoundDaoException.class, to = NotFoundException.class)
public class UsersApi extends FirebaseRegularUserAuthenticationProtectedApi {
	private UserService userService;

	@Inject
	public UsersApi(@NonNull UserService userService) {
		this.userService = userService;
	}

	@ApiMethod(httpMethod = ApiMethod.HttpMethod.GET, path = "/store/users/me", apiKeyRequired = AnnotationBoolean.TRUE)
	public UserDto getLoggedUser(@NonNull User user) throws ServiceException {
		return UserBuildCoordinator.toDto(userService.getByFirebaseId(user.getId()));
	}
}
