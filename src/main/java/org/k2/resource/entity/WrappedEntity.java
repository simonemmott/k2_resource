package org.k2.resource.entity;

import java.io.IOException;
import java.util.Arrays;

import org.k2.resource.binary.BinaryResource;
import org.k2.resource.binary.BinaryResourceItem;
import org.k2.resource.binary.BinaryEntity;
import org.k2.resource.binary.KeyDeserializer;
import org.k2.resource.binary.KeySerializer;
import org.k2.resource.entity.serialize.EntitySerialization;
import org.k2.resource.exception.UnexpectedResourceError;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

public class WrappedEntity<K,E> implements BinaryEntity {
	
	@Getter
	private E entity;
	private final EntitySerialization<K,E> serialization;
	@Getter
	private final long checksum;
	
	public WrappedEntity(
			E entity, 
			EntitySerialization<K,E> serialization) {
		this.entity = entity;
		this.serialization = serialization;
		this.checksum = BinaryResource.NEW_ENTITY;		
	}
	
	public WrappedEntity(
			byte[] data,
			EntitySerialization<K,E> serialization,
			long checksum) {
		this.serialization = serialization;
		this.entity = serialization.getDeserializer().deserialize(data);
		this.checksum = checksum;
	}

	public boolean isNew() {
		return checksum == BinaryResource.NEW_ENTITY;
	}
	public boolean isDeleted() {
		return checksum == BinaryResource.DELETED;
	}

	@Override
	public int hashCode() {
		return entity.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return entity.equals(obj);
	}

	@Override
	public String getKey() {
		return serialization.getKeySerializer().serialize(
				serialization.getKeyGetter().get(entity));
	}

	@Override
	public byte[] getData() {
		return serialization.getSerializer().serialize(entity);
	}

	@Override
	public void setKey(String keyStr) {
		serialization.getKeySetter().set(
				entity, 
				serialization.getKeyDeserializer().deserialize(keyStr));
		
	}

	@Override
	public void setData(byte[] data) {
		this.entity = serialization.getDeserializer().deserialize(data);
	}


}
