package com.store.domain.dao.client;

import java.util.List;

import com.store.domain.model.client.Client;
import com.store.domain.model.client.data.ClientCreationData;
import com.store.domain.model.client.data.ClientModificationData;

public interface ClientDao {

	public Client create(Long userId, ClientCreationData clientData);

	public Client getById(Long clientId);

	public List<Client> getList();

	public Client delete(Long clientId);

	public Client update(ClientModificationData clientData);

}
