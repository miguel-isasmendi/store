package com.store.architecture.validator;

import java.util.function.Consumer;
import java.util.function.Function;

import com.store.architecture.exception.validation.RequestBusinessValidationException;
import com.store.architecture.exception.validation.UnexpectedBuildException;
import com.store.architecture.exception.validation.UnexpectedValidationException;

import autovalue.shaded.com.google.common.common.base.Preconditions;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

@Builder
@Getter
@Accessors(chain=true)
public class ObjectBuildConversionOverseer<T, S> {

	@SuppressWarnings("rawtypes")
	public static final Function NO_OP_BUILD_CLOSURE = (overseer) -> ((ObjectBuildConversionOverseer) overseer)
			.getInputObject();

	@Setter
	private T inputObject;
	private S builtObject;

	private Consumer<ObjectBuildConversionOverseer<T, S>> preBuildValidator;
	private Consumer<ObjectBuildConversionOverseer<T, S>> postBuildValidator;
	@NonNull
	private Function<ObjectBuildConversionOverseer<T, S>, S> buildClosure;

	public void checkArgument(Boolean expression, String errorMessage) {
		try {
			Preconditions.checkArgument(expression, errorMessage);
		} catch (IllegalArgumentException exception) {
			throw new RequestBusinessValidationException(exception);
		}
	}

	public void checkNotNull(Object reference, String errorMessage, Object... errorMessageArgs) {
		try {
			Preconditions.checkNotNull(reference, errorMessage, errorMessageArgs);
		} catch (NullPointerException exception) {
			throw new RequestBusinessValidationException(exception);
		}
	}

	public void checkState(boolean expression, String errorMessage, Object... errorMessageArgs) {
		try {
			Preconditions.checkState(expression, errorMessage, errorMessageArgs);
		} catch (IllegalStateException exception) {
			throw new RequestBusinessValidationException(exception);
		}
	}

	public S execute() {
		if (inputObject == null) {
			throw new UnexpectedBuildException("The execution of the build conversion requires an inputObject");
		}

		if (preBuildValidator != null) {
			doCheckIntegrityWith(preBuildValidator);
		}

		builtObject = buildUsing(buildClosure);

		if (postBuildValidator != null) {
			doCheckIntegrityWith(postBuildValidator);
		}

		return builtObject;
	}

	protected void doCheckIntegrityWith(Consumer<ObjectBuildConversionOverseer<T, S>> validator) {
		try {
			validator.accept(this);
		} catch (RequestBusinessValidationException e) {
			throw e;
		} catch (RuntimeException e) {
			throw new UnexpectedValidationException(e);
		}
	}

	protected S buildUsing(Function<ObjectBuildConversionOverseer<T, S>, S> buildClosure) {
		try {
			return buildClosure.apply(this);
		} catch (RuntimeException e) {
			throw new UnexpectedBuildException(e);
		}
	}
}
