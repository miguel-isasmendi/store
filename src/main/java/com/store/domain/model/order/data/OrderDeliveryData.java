package com.store.domain.model.order.data;

import java.util.Date;

import com.store.domain.model.order.OrderDeliveryStatus;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class OrderDeliveryData {
	@NonNull
	private Long createdByUserId;
	@NonNull
	private Date createdOn;
	@NonNull
	private Long orderId;
	@NonNull
	private Long orderDeliveryId;
	@NonNull
	private Double amount;
	@NonNull
	private Date dueDate;
	@NonNull
	private OrderDeliveryStatus status;
}
