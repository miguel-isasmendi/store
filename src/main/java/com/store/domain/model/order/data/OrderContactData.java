package com.store.domain.model.order.data;

import java.util.Date;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class OrderContactData {
	@NonNull
	private Long orderId;
	@NonNull
	private Long orderContactId;
	@NonNull
	private Long clientId;
	@NonNull
	private String firstName;
	@NonNull
	private String lastName;
	private String email;
	@NonNull
	private Long createdByUserId;
	@NonNull
	private Date createdOn;
}
