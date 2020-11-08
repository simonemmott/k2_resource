package org.k2.resource;

import java.util.List;
import java.util.Set;

import org.k2.resource.exception.DuplicateKeyError;
import org.k2.resource.exception.EntityLockedError;
import org.k2.resource.exception.MissingKeyError;
import org.k2.resource.exception.MutatingEntityError;

public interface Resource<K,E> {
	E create(K key, E obj) throws DuplicateKeyError, MutatingEntityError;
	E get(K key) throws MissingKeyError;
	E update(K key, E obj) throws MissingKeyError, MutatingEntityError, EntityLockedError;
	E save(E obj) throws MissingKeyError, MutatingEntityError, DuplicateKeyError, EntityLockedError;
	List<E> fetch();
	E remove(K key) throws MissingKeyError, MutatingEntityError, EntityLockedError;
	void delete(E obj) throws MissingKeyError, MutatingEntityError, EntityLockedError;
	int count();
	boolean exists(K key);
	Set<K> keys();
}
