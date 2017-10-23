package com.store.architecture.exception.validation;

public class RequestBusinessValidationException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public RequestBusinessValidationException(String message) {
		super(message);
	}

	public RequestBusinessValidationException(RuntimeException exception) {
		super(exception.getMessage(), exception);
	}
}
