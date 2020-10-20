package org.k2.resource.entity.serialize;

import org.k2.resource.binary.BinaryEntityDeserializer;

public interface EntitySerializationFactory<K,E> {
	BinaryEntityDeserializer create(Class<K> keyType, Class<E> entityType);
}
