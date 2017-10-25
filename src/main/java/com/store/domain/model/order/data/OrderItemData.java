package com.store.domain.model.order.data;

import java.util.Date;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class OrderItemData {
	@NonNull
	private Long orderItemId;
	@NonNull
	private Long skuId;
	@NonNull
	private Long quantity;
	@NonNull
	private Double price;
	@NonNull
	private Long createdByUserId;
	@NonNull
	private Date createdOn;
}
