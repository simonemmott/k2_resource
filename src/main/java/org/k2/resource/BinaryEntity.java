package org.k2.resource;

import java.util.zip.Checksum;


import lombok.Getter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Getter
public class BinaryEntity {
	
	private String key = null;
	private final byte[] data;
	private final long checksum;
	
	BinaryEntity(String key, byte[] data, Checksum checksum) {
		assert key != null;
		this.key = key;
		this.data = data;
		checksum.update(data, 0, data.length);
		this.checksum = checksum.getValue();		
	}

	BinaryEntity(byte[] data, Checksum checksum) {
		this.data = data;
		checksum.update(data, 0, data.length);
		this.checksum = checksum.getValue();		
	}
	
	public void setKey(String key) {
		assert key != null;
		assert this.key == null;
		this.key = key;
	}

}
