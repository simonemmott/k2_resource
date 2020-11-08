package org.k2.resource.entity.serialize;

public interface EntityDeserializer<E> {
	E deserialize(byte[] data);

}
