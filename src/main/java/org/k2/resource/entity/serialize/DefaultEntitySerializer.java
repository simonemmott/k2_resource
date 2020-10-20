package org.k2.resource.entity.serialize;

public class DefaultEntitySerializer<E> implements EntitySerializer<E> {

	private final Class<E> entityType;
	
	public DefaultEntitySerializer (Class<E> entityType) {
		this.entityType = entityType;
	}

	@Override
	public byte[] serialize(E entity) {
		// TODO Auto-generated method stub
		return null;
	}

}
