package com.store.domain.model.order.data;

import java.util.Date;
import java.util.List;

import com.store.domain.model.order.OrderStatus;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;

@Getter
@Builder
public class OrderData {
	@NonNull
	private Long orderId;
	@NonNull
	private OrderContactData orderContact;
	@NonNull
	private OrderDeliveryData delivery;
	@NonNull
	private Long createdByUserId;
	@NonNull
	private Date createdOn;
	private Date cancelledOn;
	@NonNull
	private OrderStatus status;
	@NonNull
	private Double subtotal;
	@NonNull
	private Double deliveryCost;
	@NonNull
	private Double totalAmount;
	@NonNull
	private Double totalDiscount;
	@Singular
	@NonNull
	private List<OrderItemData> items;
	@Singular
	@NonNull
	private List<OrderDiscountData> discounts;
	@NonNull
	private OrderPaymentData payment;
}
