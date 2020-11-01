package org.k2.resource.entity.simple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.k2.resource.entity.ResourceSession;
import org.k2.resource.exception.MissingKeyError;

public class SimpleResourceSession implements ResourceSession {
	
	private class SessionEntity {
		public Object obj;
		public String checksum;
		public boolean deleted;
	}
	
	private Map<Class<?>, Map<Object, SessionEntity>> cache = new HashMap();

	public SimpleResourceSession() {
	}

	private Map<Object, SessionEntity> getEntityCache(Class<?> entityType) {
		if (!cache.containsKey(entityType)) {
			cache.put(entityType, new HashMap<>());
		}
		return cache.get(entityType);
	}
	@Override
	public <E> E get(Class<E> entityType, Object key) {
		Map<Object, SessionEntity> entityCache = getEntityCache(entityType);
		SessionEntity se = entityCache.get(key);
		if (se == null) {
			return null;
		}
		if (se.deleted) {
			return null;
		}
		return (E)se.obj;
	}

	@Override
	public boolean has(Class<?> entityType, Object key) {
		Map<Object, SessionEntity> entityCache = getEntityCache(entityType);
		SessionEntity se = entityCache.get(key);
		if (se == null) {
			return false;
		}
		if (se.deleted) {
			return false;
		}
		return true;
	}

	@Override
	public <E> void put(Class<E> entityType, Object key, E obj, String checksum) {
		Map<Object, SessionEntity> entityCache = getEntityCache(entityType);
		SessionEntity se = entityCache.get(key);
		if (se == null) {
			se = new SessionEntity();
			entityCache.put(key, se);
		}
		se.obj = obj;
		se.deleted = false;
		se.checksum = checksum;
	}

	@Override
	public <E> List<E> fetch(Class<E> entityType) {
		Map<Object, SessionEntity> entityCache = getEntityCache(entityType);
		List<E> objects = new ArrayList();
		for (Map.Entry<Object,SessionEntity> entry : entityCache.entrySet()) {
			if (!entry.getValue().deleted) {
				objects.add((E)entry.getValue().obj);
			}
		}
		return objects;
	}

	@Override
	public <E> boolean isDeleted(Class<E> entityType, Object key) {
		Map<Object, SessionEntity> entityCache = getEntityCache(entityType);
		SessionEntity se = entityCache.get(key);
		if (se == null) {
			return false;
		}
		return se.deleted;
	}

	@Override
	public <E> E delete(Class<E> entityType, Object key) throws MissingKeyError {
		Map<Object, SessionEntity> entityCache = getEntityCache(entityType);
		SessionEntity se = entityCache.get(key);
		if (se == null) {
			throw new MissingKeyError("Unable to delete and entity which is not loaded into the session");
		}
		se.deleted = true;
		return (E)se.obj;
	}

	@Override
	public <K,E> Set<K> keys(Class<E> entityType, Class<K> keyType) {
		Map<Object, SessionEntity> entityCache = getEntityCache(entityType);
		Set<K> keys = new HashSet();
		for (Map.Entry<Object,SessionEntity> entry : entityCache.entrySet()) {
			if (!entry.getValue().deleted) {
				keys.add((K)entry.getKey());
			}
		}
		return keys;
	}

	@Override
	public <E> String checksum(Class<E> entityType, Object key) throws MissingKeyError {
		Map<Object, SessionEntity> entityCache = getEntityCache(entityType);
		SessionEntity se = entityCache.get(key);
		if (se == null) {
			throw new MissingKeyError("Unable to ge the checksum for an entity which is not loaded into the session");
		}
		return se.checksum;
	}

}
