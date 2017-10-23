package com.store.architecture.exception.service;

public class EntityNotFoundServiceException extends ServiceException {
	private static final long serialVersionUID = 1L;

	public EntityNotFoundServiceException(String message) {
		super(message);
	}

}
