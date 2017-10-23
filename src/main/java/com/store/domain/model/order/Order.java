package com.store.domain.model.order;

import java.io.Serializable;
import java.util.List;

import com.google.cloud.Timestamp;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.Singular;

@Getter
@Builder
public class Order implements Serializable {
	private static final long serialVersionUID = 1L;

	@NonNull
	private Long orderId;
	@NonNull
	private OrderContact orderContact;
	@NonNull
	@Setter
	private OrderStatus status;
	@NonNull
	@Singular
	private List<OrderItem> items;
	@NonNull
	private OrderDelivery delivery;
	@NonNull
	private OrderNote note;
	@NonNull
	private OrderPayment payment;
	@NonNull
	@Singular
	private List<OrderDiscount> discounts;
	@NonNull
	private Long subtotal;
	@NonNull
	private Long deliveryCost;
	@NonNull
	private Long totalAmount;
	@NonNull
	private Long discountCost;
	@NonNull
	private Long createdByUserId;
	@NonNull
	private Timestamp createdOn;
	private Timestamp cancelledOn;

}
