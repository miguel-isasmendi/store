package com.store.domain.model.order.data;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;

@Getter
@Builder
public class OrderCreationCoordinatorData {
	@NonNull
	private OrderContactCreationData contact;
	@NonNull
	private OrderCreationDeliveryData delivery;
	@Singular
	@NonNull
	private List<OrderItemCreationCoordinatorData> items;
	@Singular
	@NonNull
	private List<OrderDiscountCreationData> discounts;
}
