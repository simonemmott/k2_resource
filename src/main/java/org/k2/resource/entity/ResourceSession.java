package org.k2.resource.entity;

import java.util.List;
import java.util.Set;

import org.k2.resource.Session;
import org.k2.resource.exception.MissingKeyError;
import org.k2.resource.exception.MutatingEntityError;
import org.k2.resource.transaction.ResourceTransaction;

public interface ResourceSession extends Session {

	<E> E get(Class<E> entityType, Object key);

	boolean has(Class<?> entityType, Object key);

	<E> void put(Class<E> entityType, Object key, E obj, String checksum);

	<E> Set<E> fetch(Class<E> entityType);

	<E> boolean isDeleted(Class<E> entityType, Object key);

	<E> E delete(Class<E> entityType, Object key) throws MissingKeyError, MutatingEntityError;

	<K,E> Set<K> keys(Class<E> entityType, Class<K> keyType);

	<E> String checksum(Class<E> entityType, Object key) throws MissingKeyError;

	ResourceTransaction getTransaction();

}
