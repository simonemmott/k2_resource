package org.k2.resource;

import java.util.List;

import org.k2.resource.exception.DuplicateKeyError;
import org.k2.resource.exception.MissingKeyError;
import org.k2.resource.exception.MutatingEntityError;

public interface Resource<K,V> {
	V create(K key, V obj) throws DuplicateKeyError;
	V get(K key) throws MissingKeyError;
	V update(K key, V obj) throws MissingKeyError, MutatingEntityError;
	V save(V obj) throws MissingKeyError, MutatingEntityError;
	List<V> fetch();
	V delete(K key) throws MissingKeyError;
	int count();
	boolean exists(K key);
}
