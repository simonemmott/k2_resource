package org.k2.resource.entity.serialize;

import org.k2.resource.binary.BinaryEntityDeserializer;
import org.k2.resource.binary.KeyDeserializer;
import org.k2.resource.binary.KeySerializer;
import org.k2.resource.entity.MetaEntityResource;
import org.k2.resource.entity.exception.EntityConfigurationException;
import org.k2.resource.entity.exception.KeyDefinitionException;
import org.k2.resource.entity.key.DefaultKeyDeserializer;
import org.k2.resource.entity.key.DefaultKeyGetter;
import org.k2.resource.entity.key.DefaultKeySerializer;
import org.k2.resource.entity.key.DefaultKeySetter;
import org.k2.resource.entity.key.KeyGetter;
import org.k2.resource.entity.key.KeySetter;

import lombok.Getter;

public class DefaultEntitySerializationFactory<K,E> implements EntitySerializationFactory<K,E> {
	
	@Getter
	private final Class<K> keyType;
	@Getter
	private final Class<E> entityType;
	private final EntitySerializer<E> serializer;
	private final EntityDeserializer<E> deserializer;
	private final KeyGetter<K,E> keyGetter;
	private final KeySetter<K,E> keySetter;
	private final KeySerializer<K> keySerializer;
	private final KeyDeserializer<K> keyDeserializer;

	public DefaultEntitySerializationFactory(Class<K> keyType, Class<E> entityType, MetaEntityResource metaData) throws EntityConfigurationException {
		this.keyType = keyType;
		this.entityType = entityType;
		this.serializer = new DefaultEntitySerializer<E>(entityType, metaData);
		this.deserializer = new DefaultEntityDeserializer<E>(entityType);
		this.keyGetter = new DefaultKeyGetter<K,E>(keyType, entityType);
		this.keySetter = new DefaultKeySetter<K,E>(keyType, entityType);
		this.keySerializer = new DefaultKeySerializer<K>(keyType);
		this.keyDeserializer = new DefaultKeyDeserializer<K>(keyType);
	}

	@Override
	public EntitySerialization<K,E> create(Class<K> keyType, Class<E> entityType) {
		return new EntitySerialization<K,E>()
				.setSerializer(serializer)
				.setDeserializer(deserializer)
				.setKeyDeserializer(keyDeserializer)
				.setKeySerializer(keySerializer)
				.setKeyGetter(keyGetter)
				.setKeySetter(keySetter);
	}
}
