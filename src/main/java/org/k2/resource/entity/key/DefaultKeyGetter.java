package org.k2.resource.entity.key;

public class DefaultKeyGetter<K,E> implements KeyGetter<K,E> {

	private final Class<K> keyType;
	private final Class<E> entityType;
	
	public DefaultKeyGetter (Class<K> keyType, Class<E> entityType) {
		this.keyType = keyType;
		this.entityType = entityType;
	}

	@Override
	public K get(E entity) {
		// TODO Auto-generated method stub
		return null;
	}

}
