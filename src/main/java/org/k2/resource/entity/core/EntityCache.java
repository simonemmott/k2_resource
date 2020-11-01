package org.k2.resource.entity.core;

import org.k2.resource.exception.MissingKeyError;

public interface EntityCache {
	
	public final String NEW_ENTITY = "NEW_ENTITY";
	
	<E> E get(Class<E> type, Object key) throws MissingKeyError;
	<E> void put(Class<E> type, Object key, E entity);
	<E> void put(Class<E> type, Object key, E entity, String checksum);
	<E> E delete(Class<E> type, Object key) throws MissingKeyError;
	<E> boolean has(Class<E> type, Object key);
	<E> boolean isNew(Class<E> type, Object key) throws MissingKeyError;
	<E> boolean isDeleted(Class<E> type, Object key) throws MissingKeyError;
	<E> boolean isChanged(Class<E> type, Object key) throws MissingKeyError;
	<E> String checksum(Class<E> type, Object key) throws MissingKeyError;

}
