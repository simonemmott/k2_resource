package org.k2.resource.entity;

import java.util.List;
import java.util.Set;

import org.k2.resource.exception.MissingKeyError;

public interface ResourceSession {

	<E> E get(Class<E> entityType, Object key);

	boolean has(Class<?> entityType, Object key);

	<E> void put(Class<E> entityType, Object key, E obj, long checksum);

	<E> List<E> fetch(Class<E> entityType);

	<E> boolean isDeleted(Class<E> entityType, Object key);

	<E> E delete(Class<E> entityType, Object key) throws MissingKeyError;

	<K,E> Set<K> keys(Class<E> entityType, Class<K> keyType);

	<E> long checksum(Class<E> entityType, Object key) throws MissingKeyError;

}
