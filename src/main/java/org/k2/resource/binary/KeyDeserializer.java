package org.k2.resource.binary;

public interface KeyDeserializer<K> {
	K deserialize(String keyStr);
}
