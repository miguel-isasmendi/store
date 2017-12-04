package com.store.domain.model.order.build.validator;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;

import com.store.architecture.validator.ObjectBuildConversionOverseer;
import com.store.domain.exception.ErrorConstants;
import com.store.domain.model.order.data.OrderCreationCoordinatorData;
import com.store.domain.model.order.dto.OrderContactCreationDto;
import com.store.domain.model.order.dto.OrderCreationDeliveryDto;
import com.store.domain.model.order.dto.OrderCreationDto;
import com.store.domain.model.order.dto.OrderDiscountCreationDto;
import com.store.domain.model.order.dto.OrderItemCreationDto;

public class OrderCreationValidatorProvider {

	public static void validateDtoToDataTranslation(
			final ObjectBuildConversionOverseer<OrderCreationDto, OrderCreationCoordinatorData> overseer) {
		OrderCreationDto order = overseer.getInputObject();

		overseer.checkArgument(!CollectionUtils.isEmpty(order.getItems()),
				ErrorConstants.formatError(ErrorConstants.SHOULD_INCLUDE_ITEMS, "items"));

		for (OrderItemCreationDto orderItemRegistrationDto : order.getItems()) {
			checkItemIntegrity(overseer, orderItemRegistrationDto);
		}

		if (order.getDiscounts() != null) {
			for (OrderDiscountCreationDto discount : order.getDiscounts()) {
				checkDiscountIntegrity(overseer, discount);
			}
		}

		checkContactIntegrity(overseer, order.getContact());
		checkDeliveryIntegrity(overseer, order.getDelivery());
	}

	private static void checkItemIntegrity(
			ObjectBuildConversionOverseer<OrderCreationDto, OrderCreationCoordinatorData> overseer,
			OrderItemCreationDto orderItemCreationDto) {
		overseer.checkArgument(orderItemCreationDto.getQuantity() != null && orderItemCreationDto.getQuantity() > 0,
				ErrorConstants.formatError(ErrorConstants.ATTRIBUTE_SHOULD_BE_GREATER_THAN_ZERO, "quantity"));
	}

	private static void checkDeliveryIntegrity(
			ObjectBuildConversionOverseer<OrderCreationDto, OrderCreationCoordinatorData> overseer,
			OrderCreationDeliveryDto delivery) {

		overseer.checkArgument(delivery.getAmount() != null && delivery.getAmount() >= 0,
				ErrorConstants.formatError(ErrorConstants.ATTRIBUTE_SHOULD_BE_GREATER_EQUAL_TO_ZERO, "amount"));
	}

	private static void checkContactIntegrity(
			ObjectBuildConversionOverseer<OrderCreationDto, OrderCreationCoordinatorData> overseer,
			OrderContactCreationDto contact) {

		overseer.checkArgument(!StringUtils.isBlank(StringUtils.stripToEmpty(contact.getFirstName())),
				ErrorConstants.formatError(ErrorConstants.ATTRIBUTE_SHOULD_NOT_BE_EMPTY, "firstName"));

		overseer.checkArgument(!StringUtils.stripToEmpty(contact.getLastName()).isEmpty(),
				ErrorConstants.formatError(ErrorConstants.ATTRIBUTE_SHOULD_NOT_BE_EMPTY, "lastName"));

		if (contact.getEmail() != null) {
			if (!StringUtils.isEmpty(StringUtils.stripToEmpty(contact.getEmail()))) {
				overseer.checkArgument(
						EmailValidator.getInstance().isValid(StringUtils.stripToEmpty(contact.getEmail())),
						ErrorConstants.formatError(ErrorConstants.EMAIL_FORMAT_ERROR, "email"));
			}
		}

	}

	private static void checkDiscountIntegrity(
			ObjectBuildConversionOverseer<OrderCreationDto, OrderCreationCoordinatorData> overseer,
			OrderDiscountCreationDto discount) {
		overseer.checkArgument(discount.getAmount() != null && discount.getAmount() > 0,
				ErrorConstants.formatError(ErrorConstants.ATTRIBUTE_SHOULD_BE_GREATER_THAN_ZERO, "amount"));
	}

}
