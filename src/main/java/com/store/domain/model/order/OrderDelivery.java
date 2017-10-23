package com.store.domain.model.order;

import java.io.Serializable;

import com.google.cloud.Timestamp;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class OrderDelivery implements Serializable {
	private static final long serialVersionUID = 1L;

	@NonNull
	private Long orderId;
	@NonNull
	private Long orderDeliveryId;
	@NonNull
	private Long orderDeliveryMethodId;
	@NonNull
	private Long amount;
	@NonNull
	private Timestamp dueDate;
	@NonNull
	private OrderDeliveryStatus status;
	@NonNull
	private String address;
	@NonNull
	private Long createdByUserId;
	@NonNull
	private Timestamp createdOn;
}
