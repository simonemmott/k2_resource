package org.k2.resource.entity;

public interface KeyGetter<K,E> {
	K get(E entity);
}
