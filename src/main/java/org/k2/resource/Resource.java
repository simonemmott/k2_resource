package org.k2.resource;

import java.util.List;

public interface Resource<K,V> {
	V create(V obj);
	V get(K key);
	V update(K key, V obj);
	List<V> fetch();
	V delete(K key);

}
