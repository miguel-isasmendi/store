package com.store.domain.architecture.validator;

import com.google.inject.ConfigurationException;
import com.store.architecture.properties.NamedPropertiesReader;

import autovalue.shaded.org.apache.commons.lang.StringUtils;

public class PasswordValidator {
	private static PasswordValidator validator;
	private Integer passwordMaxLength;
	private Integer passwordMinLength;

	private PasswordValidator() {
		try {
			String maxLength = NamedPropertiesReader.getProperty(String.class, "firebase.password.maxLength");

			this.passwordMaxLength = Integer.parseInt(maxLength);
		} catch (ConfigurationException exception) {

		}

		try {
			String minLength = NamedPropertiesReader.getProperty(String.class, "firebase.password.minLength");

			this.passwordMinLength = Integer.parseInt(minLength);
		} catch (ConfigurationException exception) {

		}
	}

	public synchronized static PasswordValidator getValidator() {
		if (validator == null) {
			validator = new PasswordValidator();
		}
		return validator;
	}

	public boolean validate(String password) {
		return StringUtils.isNotBlank(password)
				&& !((password.length() > passwordMaxLength || passwordMinLength > password.length()));
	}
}
