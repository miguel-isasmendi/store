package com.store.architecture.firebase.authenticator;

import javax.servlet.http.HttpServletRequest;

import com.google.api.server.spi.ServiceException;
import com.google.api.server.spi.auth.EspAuthenticator;
import com.google.api.server.spi.auth.common.User;
import com.google.api.server.spi.config.Authenticator;
import com.google.api.server.spi.config.Singleton;
import com.google.api.server.spi.response.UnauthorizedException;

@Singleton
public class FirebaseStoreAuthenticator implements Authenticator {

	private EspAuthenticator espAuthenticator;

	public FirebaseStoreAuthenticator() {

		this.espAuthenticator = new EspAuthenticator();
	}

	@Override
	public User authenticate(HttpServletRequest request) throws ServiceException {
		User user = espAuthenticator.authenticate(request);

		if (user == null) {
			throw new UnauthorizedException("Invalid credentials");
		}

		return user;
	}
}