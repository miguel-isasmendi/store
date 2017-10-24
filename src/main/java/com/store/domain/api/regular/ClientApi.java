package com.store.domain.api.regular;

import java.util.List;
import java.util.stream.Collectors;

import com.google.api.server.spi.auth.common.User;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.Named;
import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.NotFoundException;
import com.google.inject.Inject;
import com.store.architecture.exception.annotation.ExceptionMapping;
import com.store.architecture.exception.dao.NotFoundDaoException;
import com.store.architecture.exception.validation.RequestBusinessValidationException;
import com.store.architecture.exception.validation.UnexpectedBuildException;
import com.store.architecture.exception.validation.UnexpectedValidationException;
import com.store.architecture.validator.ObjectBuildConversionOverseer;
import com.store.domain.architecture.api.regular.FirebaseRegularUserAuthenticationProtectedApi;
import com.store.domain.model.client.build.coordinator.ClientBuildCoordinator;
import com.store.domain.model.client.build.validator.ClientCreationValidatorProvider;
import com.store.domain.model.client.data.ClientCreationData;
import com.store.domain.model.client.dto.ClientCreationDto;
import com.store.domain.model.client.dto.ClientDto;
import com.store.domain.model.client.dto.ClientModificationDto;
import com.store.domain.service.client.ClientService;
import com.store.domain.service.user.UserService;

import lombok.NonNull;

@ExceptionMapping(from = RequestBusinessValidationException.class, to = BadRequestException.class)
@ExceptionMapping(from = UnexpectedValidationException.class, to = BadRequestException.class)
@ExceptionMapping(from = UnexpectedBuildException.class, to = BadRequestException.class)
@ExceptionMapping(from = NotFoundDaoException.class, to = NotFoundException.class)
public class ClientApi extends FirebaseRegularUserAuthenticationProtectedApi {
	private ClientService clientService;
	private UserService userService;

	@Inject
	public ClientApi(@NonNull ClientService clientService, @NonNull UserService userService) {
		this.clientService = clientService;
		this.userService = userService;
	}

	@ApiMethod(httpMethod = ApiMethod.HttpMethod.GET, path = "/store/clients/{client_id}")
	public ClientDto getClient(@NonNull User user, @NonNull @Named("client_id") Long clientId) {

		return ClientBuildCoordinator.toDto(clientService.getById(clientId));
	}

	@ApiMethod(httpMethod = ApiMethod.HttpMethod.GET, path = "/store/clients")
	public List<ClientDto> getClientsList(@NonNull User user) {

		return clientService.getClientList(userService.getByFirebaseId(user.getId()).getUserId()).stream()
				.map(ClientBuildCoordinator::toDto).collect(Collectors.toList());
	}

	@ApiMethod(httpMethod = ApiMethod.HttpMethod.POST, path = "/store/clients")
	public ClientDto createClient(@NonNull User user, @NonNull ClientCreationDto clientCreationDto) {

		ObjectBuildConversionOverseer<ClientCreationDto, ClientCreationData> translationOverseer = ObjectBuildConversionOverseer
				.<ClientCreationDto, ClientCreationData>builder().inputObject(clientCreationDto)
				.preBuildValidator(ClientCreationValidatorProvider::validateDtoToDataTranslation)
				.buildClosure(ClientBuildCoordinator::buildToData).build();

		return ClientBuildCoordinator.toDto(clientService.create(userService.getByFirebaseId(user.getId()).getUserId(),
				translationOverseer.execute()));
	}

	@ApiMethod(httpMethod = ApiMethod.HttpMethod.PUT, path = "/store/clients/{client_id}")
	public ClientDto updateClient(@NonNull User user, @NonNull @Named("client_id") Long clientId,
			@NonNull ClientModificationDto clientModificationDto) {
		userService.getByFirebaseId(user.getId());

		return ClientBuildCoordinator
				.toDto(clientService.update(ClientBuildCoordinator.toData(clientId, clientModificationDto)));
	}
}
