package com.store.domain.architecture.validator;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

public class PhoneValidator {

	private static final Logger log = Logger.getLogger(PhoneValidator.class.getName());

	private static final Pattern PHONE_PATTERN = Pattern.compile("^9[0-9]{8}$");

	public static boolean isValidPhone(String phoneNumber) {
		Matcher matcher = PHONE_PATTERN.matcher(StringUtils.stripToEmpty(phoneNumber));
		if (!matcher.matches()) {
			log.log(Level.SEVERE, "phone number:[" + phoneNumber + "] is invalid");
			return false;
		}
		return true;
	}
}
