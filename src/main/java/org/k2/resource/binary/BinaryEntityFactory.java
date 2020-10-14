package org.k2.resource.binary;

import java.util.zip.CRC32;
import java.util.zip.Checksum;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BinaryEntityFactory {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(BinaryEntityFactory.class);
	
	private final ThreadLocal<Checksum> checksum = new ThreadLocal<>();


	public BinaryEntityFactory() {
	}
	
	public BinaryEntity create(String key, String data) {
		if (checksum.get() == null) {
			checksum.set(new CRC32());
		}
		checksum.get().update(data.getBytes(),0,data.getBytes().length);
		return new BinaryEntity(key, data.getBytes(), checksum.get().getValue());
	}

}
