package com.store.domain.model.order;

import java.util.Arrays;

public enum OrderDeliveryStatus {
	READY_FOR_IN_STORE_PICK_UP, READY_FOR_DELIVERY, DELIVERED;

	public static Boolean isFinalStatus(OrderDeliveryStatus status) {
		return Arrays.asList(DELIVERED).contains(status);
	}
	
	public static Boolean isInitialStatus(OrderDeliveryStatus status) {
		return !isFinalStatus(status);
	}
}
