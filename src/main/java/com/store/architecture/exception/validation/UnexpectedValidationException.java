package com.store.architecture.exception.validation;

public class UnexpectedValidationException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public UnexpectedValidationException(String message) {
		super(message);
	}

	public UnexpectedValidationException(RuntimeException exception) {
		super(exception.getMessage(), exception);
	}
}
