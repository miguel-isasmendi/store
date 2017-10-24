package com.store.domain.model.sku.build.validator;

import org.apache.commons.lang.StringUtils;

import com.store.architecture.validator.ObjectBuildConversionOverseer;
import com.store.domain.exception.ErrorConstants;
import com.store.domain.model.sku.data.SkuCreationData;
import com.store.domain.model.sku.dto.SkuCreationDto;

public class SkuCreationValidatorProvider {

	public static void validateDtoToDataTranslation(
			ObjectBuildConversionOverseer<SkuCreationDto, SkuCreationData> overseer) {
		validate(overseer.getInputObject(), overseer);
	}

	@SuppressWarnings("rawtypes")
	public static void validate(SkuCreationDto skuDto, ObjectBuildConversionOverseer overseer) {
		overseer.checkArgument(!StringUtils.isBlank(skuDto.getName()),
				ErrorConstants.formatError(ErrorConstants.ATTRIBUTE_SHOULD_NOT_BE_EMPTY, "name"));

		overseer.checkArgument(!StringUtils.isBlank(skuDto.getDescription()),
				ErrorConstants.formatError(ErrorConstants.ATTRIBUTE_SHOULD_NOT_BE_EMPTY, "description"));

		overseer.checkArgument(skuDto.getPrice() != null && skuDto.getPrice() > 0,
				ErrorConstants.formatError(ErrorConstants.ATTRIBUTE_SHOULD_BE_GREATER_THAN_ZERO, "price"));
	}
}