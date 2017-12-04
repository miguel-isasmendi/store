package com.store.domain.service.client.impl;

import java.util.List;
import java.util.stream.Collectors;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.inject.Inject;
import com.store.architecture.cache.CacheHandler;
import com.store.domain.dao.client.ClientDao;
import com.store.domain.model.client.Client;
import com.store.domain.model.client.build.coordinator.ClientBuildCoordinator;
import com.store.domain.model.client.data.ClientCreationData;
import com.store.domain.model.client.data.ClientData;
import com.store.domain.model.client.data.ClientModificationData;
import com.store.domain.service.client.ClientService;

import lombok.NonNull;

public class CachedClientService implements ClientService {
	private static final String CLIENT_CACHE_PREFIX = "client_";

	private ClientDao clientDao;
	private CacheHandler<Client> clientCacheHandler;

	@Inject
	public CachedClientService(@NonNull ClientDao clientDao, @NonNull MemcacheService cache) {
		this.clientDao = clientDao;

		this.clientCacheHandler = CacheHandler.<Client>builder().cache(cache)
				.keyGeneratorClosure(element -> element.getClientId()).prefix(CLIENT_CACHE_PREFIX).build();
	}

	@Override
	public ClientData create(@NonNull Long userId, @NonNull ClientCreationData clientData) {
		Client newClient = clientDao.create(userId, clientData);

		putClientIntoCache(newClient);

		return ClientBuildCoordinator.toData(newClient);
	}

	@Override
	public ClientData getById(@NonNull Long clientId) {
		Client client = clientCacheHandler.getFromCacheUsingPartialKey(clientId);

		if (client == null) {
			client = clientDao.getById(clientId);

			putClientIntoCache(client);
		}

		return ClientBuildCoordinator.toData(client);
	}

	@Override
	public List<ClientData> getClientList(@NonNull Long userId) {
		List<Long> clientsIds = clientDao.getClientsIds();

		return clientsIds.stream().map(this::getById).collect(Collectors.toList());
	}

	@Override
	public void deleteClient(Long clientId) {
		Client client = clientDao.delete(clientId);

		clientCacheHandler.deleteFromCache(client);
	}

	@Override
	public ClientData update(ClientModificationData clientData) {
		Client client = clientDao.update(clientData);

		putClientIntoCache(client);

		return ClientBuildCoordinator.toData(client);
	}

	private void putClientIntoCache(Client client) {
		clientCacheHandler.putIntoCache(client);
	}

}
