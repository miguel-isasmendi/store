package com.store.domain.exception;

public class ErrorConstants {
	public static final String SHOULD_INCLUDE_ITEMS = "The attribute %s should include items";
	public static final String ATTRIBUTE_SHOULD_BE_GREATER_EQUAL_TO_ZERO = "The attribute %s should be greater or equal to zero";
	public static final String ATTRIBUTE_SHOULD_BE_GREATER_THAN_ZERO = "The attribute %s should be greater than zero";
	public static final String ATTRIBUTE_SHOULD_BE_GREATER_THAN = "The attribute %s should have a value greater than %s";
	public static final String ATTRIBUTE_SHOULD_BE_INCLUDED = "The attribute %s should have a value";
	public static final String ATTRIBUTE_SHOULD_NOT_BE_EMPTY = "The attribute %s is empty";
	public static final String ATTRIBUTE_SHOULD_NOT_BE_EMPTY_FOR_OBJECT = "El %s should include at least one %s";
	public static final String PASSWORD_FORMAT_ERROR = "The password doesn't have the proper format required";
	public static final String EMAIL_FORMAT_ERROR = "The %s doesn't have the format required";
	public static final String SHOULD_BE_LESSER_EQUAL_THAN = "%s should be less or equal to %s";

	public static final String CLIENT_WITH_SAME_NAME_ALREADY_EXISTS = "There's already a client with the given name. please retry with different data.";
	public static final String ELEMENT_ALREADY_EXISTS = "The object %s already exists with the attributes received";
	public static final String ELEMENT_NOT_FOUND_FOR_ARGUMENTS = "Can't find the element %s with the seach criteria (%s)";
	public static final String SAME_ELEMENT_CONFLICT = "There is already a %s with the same attributes";
	public static final String CANT_INVALIDATE_ELEMENT_UNTIL_ACTIVE = "The %s to invalidate should be active";
	public static final String CANT_SET_STATUS = "Can't set status from %s to %s";
	public static final String STATUS_RECEIVED_SHOULD_BE = "The status of %s should be one of the following (%s)";
	public static final String ELEMENT_ALREADY_CANCELED = "%s already canceled";
	public static final String PAYMENT_CANT_BE_DELETED_ORDER_CANCELLED = "The paymente can't be deleted since order is already cancelled";
	public static final String PAYMENT_CANT_BE_DELETED_ORDER_COMPLETED = "The paymente can't be deleted since order is already completed";
	public static final String THERE_ARE_NO_PAYMENTS_TO_DELETE = "There are no payments to delete since the order is new";
	public static final String SOME_ELEMENTS_DOESNT_EXISTS = "Some %s doesn't exist";
	public static final String THE_SUM_OF_EXCEEDS_SOMETHING = "The sum of %s exceeds %s";
	public static final String DOESNT_MATCH_VALID_STATUS = "The statu values for this process are (%s)";
	public static final String THE_ONLY_VALID_VALUE_IS = "The only valid value is %s";

	public static final String AMOUNT_OF_DISCOUNTS_EXCEEDS_ORDER_TOTAL = "The amount of discounts should be less or equal to the total amount of the order";
	public static final String SUM_OF_PAYMENTS_EXCEEDS_TOTAL_AMOUNT = "The sum of the payments informed exceeds the order total";
	public static final String CANT_SET_STATUS_ORDER_SINCE = "Unable to update order status since %s";

	public static final String THE_USER_HAS_A_PENDING_ACCEPTANCE_VERIFICATION_CODE = "THE_USER_HAS_A_PENDING_ACCEPTANCE_VERIFICATION_CODE";
	public static final String THE_USER_HAS_AN_EXPIRED_VERIFICATION_CODE = "THE_USER_HAS_AN_EXPIRED_VERIFICATION_CODE";
	public static final String THE_VALIDATION_CODE_RECEIVED_IS_INVALID = "THE_VALIDATION_CODE_RECEIVED_IS_INVALID";
	public static final String THE_USER_HAS_ALREADY_CONFIRMED_THE_MAIL = "THE_USER_HAS_ALREADY_CONFIRMED_THE_MAIL";
	public static final String SHOULD_NOT_HAVE_REPEATED_ELEMENTS = "%s shouldn't have repeated values";

	public static String formatError(String errorString, Object... errorArguments) {
		return new String(String.format(errorString, errorArguments));
	}
}
