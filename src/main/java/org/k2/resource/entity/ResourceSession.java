package org.k2.resource.entity;

public interface ResourceSession {

	<K> K get(Class<K> entityType, Object key);

	boolean has(Class<?> entityType, Object key);

	<E> void put(Class<E> entityType, Object key, E obj, long checksum);

}
