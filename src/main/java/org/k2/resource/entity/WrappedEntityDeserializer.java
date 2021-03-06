package org.k2.resource.entity;

import org.k2.resource.binary.BinaryEntityDeserializer;
import org.k2.resource.entity.serialize.EntitySerialization;

public class WrappedEntityDeserializer<K,E> implements BinaryEntityDeserializer {
	
	private final EntitySerialization<K,E> serialization;

	public WrappedEntityDeserializer(EntitySerialization<K,E> serialization) {
		this.serialization = serialization;
	}

	@Override
	public WrappedEntity<K,E> deserialize(String key, byte[] data, String checksum) {
		return new WrappedEntity<>(data, serialization, checksum);
	}

}
