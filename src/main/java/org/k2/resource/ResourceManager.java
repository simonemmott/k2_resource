package org.k2.resource;

import org.k2.resource.entity.exception.ManagedResourceError;

public interface ResourceManager {
	
	Session getSession();
	<K,E> Resource<K,E> getResource(Class<K> keyType, Class<E> entityType) throws ManagedResourceError;
	<E> Resource<?,E> getResource(Class<E> entityType) throws ManagedResourceError;

}
