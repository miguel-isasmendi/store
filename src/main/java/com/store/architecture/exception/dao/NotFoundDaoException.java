package com.store.architecture.exception.dao;

public class NotFoundDaoException extends DaoException {
	private static final long serialVersionUID = 1L;

	public NotFoundDaoException(String message) {
		super(message);
	}

}
