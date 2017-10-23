package com.store.architecture.exception.dao;

public class ConflictDaoException extends CreationDaoException {
	private static final long serialVersionUID = 1L;

	public ConflictDaoException(String message) {
		super(message);
	}
}
