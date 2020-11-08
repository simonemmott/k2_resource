package org.k2.resource.entity.serialize;

import org.k2.resource.entity.exception.UnexpectedSerializationError;

public interface EntitySerializer<E> {
	byte[] serialize(E entity);

}
