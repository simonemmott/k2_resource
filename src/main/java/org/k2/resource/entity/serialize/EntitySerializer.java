package org.k2.resource.entity.serialize;

public interface EntitySerializer<E> {
	byte[] serialize(E entity);

}
