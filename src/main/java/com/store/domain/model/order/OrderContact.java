package com.store.domain.model.order;

import java.io.Serializable;

import com.google.cloud.Timestamp;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class OrderContact implements Serializable {
	private static final long serialVersionUID = 1L;

	@NonNull
	private Long orderId;
	@NonNull
	private Long orderContactId;
	@NonNull
	private Long clientId;
	@NonNull
	private String phone;
	@NonNull
	private Long createdByUserId;
	@NonNull
	private Timestamp createdOn;
	private String email;
}
