package com.store.domain.model.order;

import java.io.Serializable;

import com.google.cloud.Timestamp;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class OrderItem implements Serializable {
	private static final long serialVersionUID = 1L;

	@NonNull
	private Long orderItemId;
	@NonNull
	private Long orderId;
	@NonNull
	private Long createdByUserId;
	@NonNull
	private Timestamp createdOn;
	@NonNull
	private Long skuId;
	@NonNull
	private Double price;
	@NonNull
	private Long quantity;
}
