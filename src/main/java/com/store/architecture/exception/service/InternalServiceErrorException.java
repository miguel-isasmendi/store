package com.store.architecture.exception.service;

public class InternalServiceErrorException extends ServiceException {
	private static final long serialVersionUID = 1L;

	public InternalServiceErrorException(String message) {
		super(message);
	}

	public InternalServiceErrorException(Throwable e) {
		super(e);
	}

}
