package org.k2.resource.entity.key;

public class DefaultKeySetter<K,E> implements KeySetter<K,E> {
	
	private final Class<K> keyType;
	private final Class<E> entityType;
	
	public DefaultKeySetter (Class<K> keyType, Class<E> entityType) {
		this.keyType = keyType;
		this.entityType = entityType;
	}

	@Override
	public void set(E entity, K key) {
		// TODO Auto-generated method stub
		
	}

}
