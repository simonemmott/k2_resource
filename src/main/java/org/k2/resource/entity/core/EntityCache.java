package org.k2.resource.entity.core;

import java.util.Set;

import org.k2.resource.entity.core.SimpleEntityCache.CacheItem;
import org.k2.resource.exception.DuplicateKeyError;
import org.k2.resource.exception.EntityLockedError;
import org.k2.resource.exception.MissingKeyError;
import org.k2.resource.exception.MutatingEntityError;

public interface EntityCache {
	
	public interface CacheItemConsumer {
		void accept(
				Class<?> keyType, 
				Object key, 
				Class<?> entityType, 
				Object entity, 
				boolean isNew, 
				boolean isChanged, 
				boolean isDeleted,
				String checksum) throws 
		MissingKeyError, 
		MutatingEntityError, 
		DuplicateKeyError,
		EntityLockedError;
	}
	
	public final String NEW_ENTITY = "NEW_ENTITY";
	
	<E> E get(Class<E> type, Object key) throws MissingKeyError;
	<E> void put(Class<E> type, Object key, E entity) throws MutatingEntityError;
	<E> void put(Class<E> type, Object key, E entity, String checksum);
	<E> E delete(Class<E> type, Object key) throws MissingKeyError, MutatingEntityError;
	<E> boolean has(Class<E> type, Object key);
	<E> boolean isNew(Class<E> type, Object key) throws MissingKeyError;
	<E> boolean isDeleted(Class<E> type, Object key) throws MissingKeyError;
	<E> boolean isChanged(Class<E> type, Object key) throws MissingKeyError;
	<E> String checksum(Class<E> type, Object key) throws MissingKeyError;
	<E> Set<E> fetch(Class<E> entityType);
	<E> Set<Object> keys(Class<E> type);
	<K,E> Set<K> keys(Class<E> type, Class<K> keyType);
	void forEach(CacheItemConsumer consumer) throws 
			MissingKeyError, 
			MutatingEntityError, 
			DuplicateKeyError, 
			EntityLockedError;

}
