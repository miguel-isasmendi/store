package com.store.architecture.cache;

import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;

import com.google.appengine.api.memcache.MemcacheService;
import com.store.architecture.exception.service.InvalidArgumentsServiceException;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@Builder
public class CacheHandler<T> {
	@NonNull
	private MemcacheService cache;
	private Function<T, Object> keyGeneratorClosure;
	private String prefix;

	public T putIntoCache(T element) {
		cache.put(generateKeyFor(element), element);
		return element;
	}

	public T putIntoCacheUsingPartialKey(Object partialKey, T element) {
		cache.put(prependKeyPrefixTo(partialKey), element);
		return element;
	}

	public T putIntoCacheUsingPartialKey(T element) {
		if (keyGeneratorClosure != null) {
			return putIntoCache(element);
		} else {
			return putIntoCacheUsingPartialKey(null, element);
		}
	}

	private void checkRequiredKeyGeneratorClosure() {
		if (keyGeneratorClosure == null) {
			throw new InvalidArgumentsServiceException("This method requires an existent key generator closure!");
		}
	}

	public boolean deleteFromCache(T element) {
		return cache.delete(generateKeyFor(element));
	}

	public boolean deleteFromCacheUsingPartialKey(Object elementKey) {
		return cache.delete(prependKeyPrefixTo(elementKey));
	}

	@SuppressWarnings("unchecked")
	public T getFromCache(T element) {
		return (T) cache.get(generateKeyFor(element));
	}

	@SuppressWarnings("unchecked")
	public T getFromCacheUsingPartialKey(Object elementKey) {
		return (T) cache.get(prependKeyPrefixTo(elementKey));
	}

	public T getFromCache() {
		if (keyGeneratorClosure == null) {
			return getFromCacheUsingPartialKey(null);
		} else {
			return getFromCache(null);
		}
	}

	public String generateKeyFor(T element) {
		checkRequiredKeyGeneratorClosure();
		return prependKeyPrefixTo(keyGeneratorClosure.apply(element));
	}

	public String prependKeyPrefixTo(Object keyElement) {
		String processedKeyElement = keyElement == null ? StringUtils.EMPTY : keyElement.toString();

		return getPrefix() + processedKeyElement;
	}

	public String getPrefix() {
		if (prefix == null) {
			prefix = StringUtils.EMPTY;
		}
		return prefix;
	}

}
