package com.store.domain.model.order.data;

import java.util.Date;
import java.util.List;

import com.store.domain.model.order.OrderPaymentStatus;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;

@Getter
@Builder
public class OrderPaymentData {
	@NonNull
	private Long orderPaymentId;
	@NonNull
	private Long createdByUserId;
	@NonNull
	private Date createdOn;
	@NonNull
	private Long orderId;
	@NonNull
	private Double totalAmount;
	@NonNull
	private OrderPaymentStatus status;
	@NonNull
	@Singular
	private List<OrderPaymentItemData> items;
}
