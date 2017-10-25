package com.store.domain.model.order.build.coordinator;

import org.apache.commons.lang.StringUtils;

import com.store.architecture.utils.DateUtils;
import com.store.domain.model.order.OrderContact;
import com.store.domain.model.order.data.OrderContactCreationData;
import com.store.domain.model.order.data.OrderContactData;
import com.store.domain.model.order.dto.OrderContactCreationDto;
import com.store.domain.model.order.dto.OrderContactDto;

public class OrderContactBuildCoordinator {

	public static OrderContactCreationData toData(OrderContactCreationDto contact) {
		return OrderContactCreationData.builder().clientId(contact.getClientId())
				.firstName(StringUtils.stripToEmpty(contact.getFirstName()))
				.lastName(StringUtils.stripToEmpty(contact.getLastName()))
				.email(StringUtils.stripToEmpty(contact.getEmail())).build();
	}

	public static OrderContactData toData(OrderContact orderContact) {
		return OrderContactData.builder().clientId(orderContact.getClientId()).firstName(orderContact.getFirstName())
				.lastName(orderContact.getLastName()).orderId(orderContact.getOrderId())
				.orderContactId(orderContact.getOrderContactId()).createdByUserId(orderContact.getCreatedByUserId())
				.email(orderContact.getEmail()).createdOn(DateUtils.dateFrom(orderContact.getCreatedOn())).build();
	}

	public static OrderContactDto toDto(OrderContactData orderContact) {
		return OrderContactDto.builder().clientId(orderContact.getClientId()).firstName(orderContact.getFirstName())
				.lastName(orderContact.getLastName()).orderId(orderContact.getOrderId())
				.orderContactId(orderContact.getOrderContactId()).createdByUserId(orderContact.getCreatedByUserId())
				.email(orderContact.getEmail()).createdOn(orderContact.getCreatedOn()).build();
	}
}
