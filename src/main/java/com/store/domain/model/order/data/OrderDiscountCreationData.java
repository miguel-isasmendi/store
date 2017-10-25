package com.store.domain.model.order.data;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class OrderDiscountCreationData {
	@NonNull
	private Double amount;
}
