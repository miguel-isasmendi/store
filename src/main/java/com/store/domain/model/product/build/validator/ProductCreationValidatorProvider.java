package com.store.domain.model.product.build.validator;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.store.architecture.validator.ObjectBuildConversionOverseer;
import com.store.domain.exception.ErrorConstants;
import com.store.domain.model.product.data.ProductCreationData;
import com.store.domain.model.product.dto.ProductCreationDto;
import com.store.domain.model.sku.build.validator.SkuCreationValidatorProvider;

public class ProductCreationValidatorProvider {
	public static void validateDtoToDataTranslation(
			ObjectBuildConversionOverseer<ProductCreationDto, ProductCreationData> overseer) {
		ProductCreationDto productDto = overseer.getInputObject();
		overseer.checkArgument(!StringUtils.isBlank(productDto.getName()),
				ErrorConstants.formatError(ErrorConstants.ATTRIBUTE_SHOULD_NOT_BE_EMPTY, "name"));

		overseer.checkArgument(!StringUtils.isBlank(productDto.getDescription()),
				ErrorConstants.formatError(ErrorConstants.ATTRIBUTE_SHOULD_NOT_BE_EMPTY, "description"));

		overseer.checkArgument(!CollectionUtils.isEmpty(productDto.getSkus()),
				ErrorConstants.formatError(ErrorConstants.ATTRIBUTE_SHOULD_NOT_BE_EMPTY_FOR_OBJECT, "product", "skus"));

		productDto.getSkus().stream()
				.forEach(skuCreationDto -> SkuCreationValidatorProvider.validate(skuCreationDto, overseer));
	}

}
