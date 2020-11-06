package org.k2.resource.entity.simple;

import java.util.Set;

import org.k2.resource.entity.ResourceSession;
import org.k2.resource.entity.core.EntityCache;
import org.k2.resource.entity.core.SimpleEntityCache;
import org.k2.resource.exception.MissingKeyError;
import org.k2.resource.exception.MutatingEntityError;

public class SimpleResourceSession implements ResourceSession {
	
	private EntityCache cache = new SimpleEntityCache();

	public SimpleResourceSession() {}

	@Override
	public <E> E get(Class<E> entityType, Object key) {
		try {
			return cache.get(entityType, key);
		} catch (MissingKeyError e) {
			return null;
		}
	}

	@Override
	public boolean has(Class<?> entityType, Object key) {
		return cache.has(entityType, key);
	}

	@Override
	public <E> void put(Class<E> entityType, Object key, E obj, String checksum) {
		cache.put(entityType, key, obj, checksum);
	}

	@Override
	public <E> Set<E> fetch(Class<E> entityType) {
		return cache.fetch(entityType);
	}

	@Override
	public <E> boolean isDeleted(Class<E> entityType, Object key){
		try {
			return cache.isDeleted(entityType, key);
		} catch (MissingKeyError e) {
			return false;
		}
	}

	@Override
	public <E> E delete(Class<E> entityType, Object key) throws MissingKeyError, MutatingEntityError {
		if (!cache.has(entityType, key)) {
			throw new MissingKeyError("Unable to delete and entity which is not loaded into the session");
		}
		E entity = cache.get(entityType, key);
		cache.delete(entityType, key);
		return entity;
	}

	@Override
	public <K,E> Set<K> keys(Class<E> entityType, Class<K> keyType) {
		return cache.keys(entityType, keyType);
	}

	@Override
	public <E> String checksum(Class<E> entityType, Object key) throws MissingKeyError {
		return cache.checksum(entityType, key);
	}

}
