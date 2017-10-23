package com.store.architecture.exception.service;

public class ConflictServiceException extends CreationServiceException {
	private static final long serialVersionUID = 1L;

	public ConflictServiceException(String message) {
		super(message);
	}
}
