package com.store.architecture.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Singular;

@Getter
public class RequestListContainer<T> {

	@Singular
	private List<T> items = new ArrayList<T>();
}
