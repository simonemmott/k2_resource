package org.k2.resource.binary;

public class DefaultBinaryEntityDeserializer implements BinaryEntityDeserializer {

	@Override
	public BinaryEntity deserialize(String key, byte[] data, long checksum) {
		return new BinaryEntityImpl(key, data, checksum);
	}

}
