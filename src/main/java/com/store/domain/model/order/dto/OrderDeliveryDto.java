package com.store.domain.model.order.dto;

import java.util.Date;

import com.store.domain.model.order.OrderDeliveryStatus;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class OrderDeliveryDto {
	@NonNull
	private Long createdByUserId;
	@NonNull
	private Date createdOn;
	@NonNull
	private Long orderId;
	@NonNull
	private Long deliveryId;
	@NonNull
	private Double amount;
	@NonNull
	private Date dueDate;
	@NonNull
	private OrderDeliveryStatus status;
}
