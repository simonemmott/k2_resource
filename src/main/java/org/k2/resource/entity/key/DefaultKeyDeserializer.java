package org.k2.resource.entity.key;

import org.k2.resource.binary.KeyDeserializer;
import org.k2.resource.binary.KeySerializer;

public class DefaultKeyDeserializer<K> implements KeyDeserializer<K> {

	private final Class<K> keyType;
	
	public DefaultKeyDeserializer (Class<K> keyType) {
		this.keyType = keyType;
	}

	@Override
	public K deserialize(String keyStr) {
		// TODO Auto-generated method stub
		return null;
	}

}
