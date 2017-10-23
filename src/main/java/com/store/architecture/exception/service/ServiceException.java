package com.store.architecture.exception.service;

import com.store.architecture.exception.BusinessException;

public abstract class ServiceException extends BusinessException {
	private static final long serialVersionUID = 1L;

	public ServiceException(String message) {
		super(message);
	}

	public ServiceException(Throwable cause) {
		super(cause);
	}
}
