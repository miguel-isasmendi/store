package com.store.domain.model.client.build.coordinator;

import com.store.architecture.utils.DateUtils;
import com.store.architecture.validator.ObjectBuildConversionOverseer;
import com.store.domain.model.client.Client;
import com.store.domain.model.client.data.ClientCreationData;
import com.store.domain.model.client.data.ClientData;
import com.store.domain.model.client.data.ClientModificationData;
import com.store.domain.model.client.dto.ClientCreationDto;
import com.store.domain.model.client.dto.ClientDto;
import com.store.domain.model.client.dto.ClientModificationDto;

import lombok.NonNull;

public class ClientBuildCoordinator {

	public static ClientData toData(@NonNull Client client) {
		return ClientData.builder().clientId(client.getClientId()).createdByUserId(client.getCreatedByUserId())
				.createdOn(DateUtils.dateFrom(client.getCreatedOn())).email(client.getEmail())
				.firstName(client.getFirstName()).lastName(client.getLastName()).build();
	}

	public static ClientDto toDto(@NonNull ClientData client) {
		return ClientDto.builder().clientId(client.getClientId()).createdByUserId(client.getCreatedByUserId())
				.createdOn(client.getCreatedOn()).email(client.getEmail()).firstName(client.getFirstName())
				.lastName(client.getLastName()).build();
	}

	public static ClientCreationData buildToData(
			@NonNull ObjectBuildConversionOverseer<ClientCreationDto, ClientCreationData> overseer) {
		return toData(overseer.getInputObject());
	}

	public static ClientCreationData toData(@NonNull ClientCreationDto clientCreationDto) {
		return ClientCreationData.builder().email(clientCreationDto.getEmail())
				.firstName(clientCreationDto.getFirstName()).lastName(clientCreationDto.getLastName()).build();
	}

	public static ClientModificationData toData(@NonNull Long clientId,
			@NonNull ClientModificationDto clientModificationDto) {
		return ClientModificationData.builder().clientId(clientId).email(clientModificationDto.getEmail())
				.firstName(clientModificationDto.getFirstName()).lastName(clientModificationDto.getLastName()).build();
	}
}
