package com.store.domain.model.bundle.build.validator;

import java.util.Date;

import com.store.architecture.validator.ObjectBuildConversionOverseer;
import com.store.domain.model.bundle.data.BundleCreationData;
import com.store.domain.model.bundle.dto.BundleCreationDto;
import com.store.domain.model.bundle.dto.BundleCreationItemDto;

public class BundleCreationDataValidatorProvider

{
	public static void validateDtoToDataTranslation(
			ObjectBuildConversionOverseer<BundleCreationDto, BundleCreationData> overseer) {
		BundleCreationDto bundle = overseer.getInputObject();

		overseer.checkArgument(bundle.getActiveFrom() != null && new Date().compareTo(bundle.getActiveFrom()) <= 0,
				"active date should be greather than the current date");

		overseer.checkArgument(bundle.getActiveUntil() != null && bundle.getActiveUntil().after(new Date()),
				"active date should be greather than the current date");

		overseer.checkArgument(bundle.getItems() != null, "items is null");
		for (BundleCreationItemDto item : bundle.getItems()) {
			checkIntegrityWith(overseer, item);
		}
	}

	private static void checkIntegrityWith(
			ObjectBuildConversionOverseer<BundleCreationDto, BundleCreationData> overseer, BundleCreationItemDto item) {
		overseer.checkArgument(item.getQuantity() != null && item.getQuantity().longValue() > 0, "sdfa");
	}

}
