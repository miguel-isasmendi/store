package com.store.architecture.exception.validation;

public class UnexpectedBuildException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public UnexpectedBuildException(String message) {
		super(message);
	}

	public UnexpectedBuildException(RuntimeException exception) {
		super(exception.getMessage(), exception);
	}
}
