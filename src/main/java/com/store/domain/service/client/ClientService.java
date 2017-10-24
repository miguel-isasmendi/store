package com.store.domain.service.client;

import java.util.List;

import com.store.domain.model.client.data.ClientCreationData;
import com.store.domain.model.client.data.ClientData;
import com.store.domain.model.client.data.ClientModificationData;

public interface ClientService {

	public ClientData create(Long userId, ClientCreationData cliData);

	public ClientData getById(Long clientId);

	public List<ClientData> getClientList(Long userId);

	public void deleteClient(Long clientId);

	public ClientData update(ClientModificationData clientData);
}
