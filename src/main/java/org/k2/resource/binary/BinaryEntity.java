package org.k2.resource.binary;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode
@Getter
public class BinaryEntity {
	
	private final String key;
	@Setter
	private byte[] data;
	private final long checksum;
	
	BinaryEntity(String key, byte[] data, long checksum) {
		assert key != null;
		this.key = key;
		this.data = data;
		this.checksum = checksum;		
	}

	BinaryEntity(BinaryResourceItem item) {
		this.key = item.getKey();
		this.data = item.getBytes();
		this.checksum = item.getChecksum();		
	}

}
