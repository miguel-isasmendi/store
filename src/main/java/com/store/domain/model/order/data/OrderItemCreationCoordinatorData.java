package com.store.domain.model.order.data;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class OrderItemCreationCoordinatorData {
	@NonNull
	private Long skuId;
	@NonNull
	private Long quantity;
}
