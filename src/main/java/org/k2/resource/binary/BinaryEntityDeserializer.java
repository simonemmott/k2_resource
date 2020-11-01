package org.k2.resource.binary;

public interface BinaryEntityDeserializer {
	BinaryEntity deserialize(String key, byte[] data, String string);
}
