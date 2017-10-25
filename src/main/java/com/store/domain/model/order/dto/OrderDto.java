package com.store.domain.model.order.dto;

import java.util.Date;
import java.util.List;

import com.store.domain.model.order.OrderStatus;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;

@Getter
@Builder
public class OrderDto {
	@NonNull
	private Long orderId;
	private OrderContactDto orderContact;
	@NonNull
	private OrderStatus status;
	@NonNull
	private OrderDeliveryDto delivery;
	@NonNull
	private Long createdByUserId;
	@NonNull
	private Date createdOn;
	private Date cancelledOn;
	@NonNull
	private Double subtotal;
	@NonNull
	private Double deliveryCost;
	@NonNull
	private Double totalAmount;
	@NonNull
	private OrderPaymentDto orderPayment;
	@NonNull
	@Singular
	private List<OrderItemDto> items;
	@NonNull
	@Singular
	private List<OrderDiscountDto> discounts;
}
