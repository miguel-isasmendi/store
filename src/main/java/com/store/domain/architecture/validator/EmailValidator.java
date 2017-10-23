package com.store.domain.architecture.validator;

public class EmailValidator {
	public static boolean validate(String email) {
		return org.apache.commons.validator.routines.EmailValidator.getInstance().isValid(email);
	}
}
