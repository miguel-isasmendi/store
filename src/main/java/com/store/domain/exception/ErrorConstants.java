package com.store.domain.exception;

public class ErrorConstants {
	public static final String SHOULD_INCLUDE_ITEMS = "The attribute %s should include items";
	public static final String ATTRIBUTE_SHOULD_BE_GREATER_EQUAL_TO_ZERO = "El atributo %s deberia tener un valor mayor o igual a cero";
	public static final String ATTRIBUTE_SHOULD_BE_GREATER_THAN_ZERO = "El atributo %s deberia tener un valor mayor a cero";
	public static final String ATTRIBUTE_SHOULD_BE_GREATER_THAN = "El atributo %s deberia tener un valor mayor a %s";
	public static final String ATTRIBUTE_SHOULD_BE_INCLUDED = "El atributo %s deberia tener un valor";
	public static final String ATTRIBUTE_SHOULD_NOT_BE_EMPTY = "El atributo %s esta vacio";
	public static final String ATTRIBUTE_SHOULD_NOT_BE_EMPTY_FOR_OBJECT = "El %s deberia incluir al menos un %s";
	public static final String PHONE_FORMAT_ERROR = "El %s debe empezar en 9, ser numerico y de 9 digitos";
	public static final String PASSWORD_FORMAT_ERROR = "La contrasena no tiene el formato requerido";
	public static final String EMAIL_FORMAT_ERROR = "El %s no tiene un formato adecuado";
	public static final String SHOULD_BE_LESSER_EQUAL_THAN = "%s deberia tener un valor menor o igual a %s";

	public static final String CLIENT_WITH_SAME_NAME_ALREADY_EXISTS = "Ya existe un cliente con el nombre recibido, por favor provea el id del cliente preexistente para operar con el mismo.";
	public static final String ELEMENT_ALREADY_EXISTS = "Ya existe %s con los atributos recibidos";
	public static final String ELEMENT_NOT_FOUND_FOR_ARGUMENTS = "No se puede encontrar el elemento %s con los criterios de busqueda (%s)";
	public static final String SAME_ELEMENT_CONFLICT = "Ya existe otro %s con los mismo atributos";
	public static final String CANT_INVALIDATE_ELEMENT_UNTIL_ACTIVE = "El %s a invalidar deveria estar en estado activo";
	public static final String CANT_SET_STATUS = "No se puede cambiar el estado desde %s a %s";
	public static final String STATUS_RECEIVED_SHOULD_BE = "El estado de %s recibido deberia ser uno de los siguientes (%s)";
	public static final String ELEMENT_ALREADY_CANCELED = "& esta cancelada";
	public static final String PAYMENT_CANT_BE_DELETED_ORDER_CANCELLED = "El pago no puede ser borrado dado que la orden ya esta cancelada";
	public static final String PAYMENT_CANT_BE_DELETED_ORDER_COMPLETED = "El pago no puede ser borrado dado que la orden ya esta completada";
	public static final String THERE_ARE_NO_PAYMENTS_TO_DELETE = "No hay pagos a eliminar dado que la orden es nueva";
	public static final String SOME_ELEMENTS_DOESNT_EXISTS = "Algunos %s no existen";
	public static final String THE_SUM_OF_EXCEEDS_SOMETHING = "La suma del %s excede %s";
	public static final String DOESNT_MATCH_VALID_STATUS = "Los estados validos para este proceso son (%s)";
	public static final String USER_SHOULD_HAVE_AT_LEAST_A_BUSINESS_ASSOCIATED = "El usuario deberia tener asociado al menos un negocio";
	public static final String SYSTEM_SHOULD_HAVE_AT_LEAST_A_DOCUMENT = "El sistema deberia tener al menos un tipo de documento disponible";
	public static final String THE_ONLY_VALID_VALUE_IS = "El unico valor valido es %s";

	public static final String AMOUNT_OF_DISCOUNTS_EXCEEDS_ORDER_TOTAL = "El monto de descuentos deberia ser menor o igual al monto total de la orden";
	public static final String SUM_OF_PAYMENTS_EXCEEDS_TOTAL_AMOUNT = "La suma de los pagos realizados excede el total de la orden";
	public static final String CANT_SET_STATUS_ORDER_SINCE = "No se puede actualizar el estado de una orden debido a %s";

	public static final String TERMS_AND_CONDITIONS_HAVE_TO_BE_ACCEPTED_AGAIN = "TERMS_AND_CONDITIONS_HAVE_TO_BE_ACCEPTED_AGAIN";
	public static final String PRIVACY_POLICIES_HAVE_TO_BE_ACCEPTED_AGAIN = "PRIVACY_POLICIES_HAVE_TO_BE_ACCEPTED_AGAIN";
	public static final String THE_USER_HAS_A_PENDING_ACCEPTANCE_VERIFICATION_CODE = "THE_USER_HAS_A_PENDING_ACCEPTANCE_VERIFICATION_CODE";
	public static final String THE_USER_HAS_AN_EXPIRED_VERIFICATION_CODE = "THE_USER_HAS_AN_EXPIRED_VERIFICATION_CODE";
	public static final String THE_VALIDATION_CODE_RECEIVED_IS_INVALID = "THE_VALIDATION_CODE_RECEIVED_IS_INVALID";
	public static final String CONSUMER_SOFTWARE_UPGRADE_NEEDED = "CONSUMER_SOFTWARE_UPGRADE_NEEDED";
	public static final String THE_USER_HAS_ALREADY_CONFIRMED_THE_MAIL = "THE_USER_HAS_ALREADY_CONFIRMED_THE_MAIL";

	public static String formatError(String errorString, Object... errorArguments) {
		return new String(String.format(errorString, errorArguments));
	}
}
