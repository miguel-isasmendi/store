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
public class OrderPayment implements Serializable {
	private static final long serialVersionUID = 1L;

	@NonNull
	private Long orderPaymentId;
	@NonNull
	private Long createdByUserId;
	@NonNull
	private Timestamp createdOn;
	@NonNull
	private Long orderId;
	@NonNull
	private Long orderPaymentMethodId;
	@NonNull
	private Long totalAmount;
	@NonNull
	@Setter
	private OrderPaymentStatus status;
	@Singular
	@NonNull
	private List<OrderPaymentItem> items;

	public Long addAmount(Long amount) {
		if (this.totalAmount == null) {
			this.totalAmount = 0l;
		}

		this.totalAmount += amount;

		return this.totalAmount;
	}
}
