package org.k2.resource.entity;

import org.k2.resource.binary.BinaryEntityDeserializer;
import org.k2.resource.binary.KeyDeserializer;
import org.k2.resource.binary.KeySerializer;

public class DefaultEntitySerializationFactory<K,E> implements EntitySerializationFactory<K,E> {
	
	private final Class<K> keyType;
	private final Class<E> entityType;
	private final EntitySerializer<E> serializer;
	private final EntityDeserializer<E> deserializer;
	private final KeyGetter<K,E> keyGetter;
	private final KeySetter<K,E> keySetter;
	private final KeySerializer<K> keySerializer;
	private final KeyDeserializer<K> keyDeserializer;

	public DefaultEntitySerializationFactory(Class<K> keyType, Class<E> entityType) {
		this.keyType = keyType;
		this.entityType = entityType;
		this.serializer = new DefaultEntitySerializer(entityType);
		this.deserializer = new DefaultEntityDeserializer(entityType);
		this.keyGetter = new DefaultKeyGetter(keyType, entityType);
		this.keySetter = new DefaultKeySetter(keyType, entityType);
		this.keySerializer = new DefaultKeySerializer(keyType);
		this.keyDeserializer = new DefaultKeyDeserializer(keyType);
	}

	@Override
	public BinaryEntityDeserializer create(Class<K> keyType, Class<E> entityType) {
		return new EntitySerialization()
				.setSerializer(serializer)
				.setDeserializer(deserializer)
				.setKeyDeserializer(keyDeserializer)
				.setKeySerializer(keySerializer)
				.setKeyGetter(keyGetter)
				.setKeySetter(keySetter);
	}

}
