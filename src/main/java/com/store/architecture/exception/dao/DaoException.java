package com.store.architecture.exception.dao;

import com.store.architecture.exception.BusinessException;

public abstract class DaoException extends BusinessException {
	private static final long serialVersionUID = 1L;

	public DaoException(String message) {
		super(message);
	}
}
