package com.store.architecture.model;

import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

@Data
@Builder
public class ResponseListContainer<T> {
	@Singular
	private List<T> items;
}
