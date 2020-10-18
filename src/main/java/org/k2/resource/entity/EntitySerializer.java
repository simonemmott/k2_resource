package org.k2.resource.entity;

public interface EntitySerializer<E> {
	byte[] serialize(E entity);

}
