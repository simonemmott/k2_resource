package org.k2.resource.entity;

public interface KeySetter<K,E> {
	void set(E entity, K key);

}
