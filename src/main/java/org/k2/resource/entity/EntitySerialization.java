package org.k2.resource.entity;

import org.k2.resource.binary.BinaryEntity;
import org.k2.resource.binary.BinaryEntityDeserializer;
import org.k2.resource.binary.KeyDeserializer;
import org.k2.resource.binary.KeySerializer;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class EntitySerialization<K,E> implements BinaryEntityDeserializer{
	
	private EntitySerializer<E> serializer;
	private EntityDeserializer<E> deserializer;
	private KeyGetter<K,E> keyGetter;
	private KeySetter<K,E> keySetter;
	private KeySerializer<K> keySerializer;
	private KeyDeserializer<K> keyDeserializer;
	
	public EntitySerialization<K,E> setSerializer(EntitySerializer<E> serializer) {
		this.serializer = serializer;
		return this;
	}
	
	public EntitySerialization<K,E> setDeserializer(EntityDeserializer<E> deserializer) {
		this.deserializer = deserializer;
		return this;
	}
	
	public EntitySerialization<K,E> setKeyGetter(KeyGetter<K,E> keyGetter) {
		this.keyGetter = keyGetter;
		return this;
	}
	
	public EntitySerialization<K,E> setKeySetter(KeySetter<K,E> keySetter) {
		this.keySetter = keySetter;
		return this;
	}
	
	public EntitySerialization<K,E> setKeySerializer(KeySerializer<K> keySerializer) {
		this.keySerializer = keySerializer;
		return this;
	}
	
	public EntitySerialization<K,E> setKeyDeserializer(KeyDeserializer<K> keyDeserializer) {
		this.keyDeserializer = keyDeserializer;
		return this;
	}
	
	@Override
	public BinaryEntity deserialize(String key, byte[] data, long checksum) {
		return new WrappedEntity<>(data, this, checksum);
	}

}