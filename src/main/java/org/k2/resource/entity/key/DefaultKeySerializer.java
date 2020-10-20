package org.k2.resource.entity.key;

import org.k2.resource.binary.KeySerializer;

public class DefaultKeySerializer<K> implements KeySerializer<K> {

	private final Class<K> keyType;
	
	public DefaultKeySerializer (Class<K> keyType) {
		this.keyType = keyType;
	}

	@Override
	public String serialize(K key) {
		// TODO Auto-generated method stub
		return null;
	}

}
