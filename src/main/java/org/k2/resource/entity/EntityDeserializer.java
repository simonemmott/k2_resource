package org.k2.resource.entity;

public interface EntityDeserializer<E> {
	E deserialize(byte[] data);

}
