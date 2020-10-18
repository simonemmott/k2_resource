package org.k2.resource.entity;

public class DefaultEntityDeserializer<E> implements EntityDeserializer<E> {

	private final Class<E> entityType;
	
	public DefaultEntityDeserializer (Class<E> entityType) {
		this.entityType = entityType;
	}

	@Override
	public E deserialize(byte[] data) {
		// TODO Auto-generated method stub
		return null;
	}

}
