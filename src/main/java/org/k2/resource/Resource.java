package org.k2.resource;

import java.util.List;
import java.util.Set;

import org.k2.resource.exception.DuplicateKeyError;
import org.k2.resource.exception.MissingKeyError;
import org.k2.resource.exception.MutatingEntityError;

public interface Resource<K,V> {
	V create(K key, V obj) throws DuplicateKeyError, MutatingEntityError;
	V get(K key) throws MissingKeyError;
	V update(K key, V obj) throws MissingKeyError, MutatingEntityError;
	V save(V obj) throws MissingKeyError, MutatingEntityError, DuplicateKeyError;
	List<V> fetch();
	V remove(K key) throws MissingKeyError, MutatingEntityError;
	void delete(V obj) throws MissingKeyError, MutatingEntityError;
	int count();
	boolean exists(K key);
	Set<K> keys();
}
