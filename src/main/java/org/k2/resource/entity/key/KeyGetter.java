package org.k2.resource.entity.key;

public interface KeyGetter<K,E> {
	K get(E entity);
}
