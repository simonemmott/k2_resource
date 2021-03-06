package org.k2.resource.binary;

import java.io.IOException;
import java.util.Arrays;

import org.k2.resource.exception.UnexpectedResourceError;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
public class BinaryEntityImpl implements BinaryEntity {
	
	@Setter
	private String key;
	@Setter
	private byte[] data;
	private final String checksum;
	
	public BinaryEntityImpl(String key, byte[] data, String checksum) {
		assert key != null;
		this.key = key;
		this.data = data;
		this.checksum = checksum;		
	}

	public BinaryEntityImpl(String key, byte[] data) {
		assert key != null;
		this.key = key;
		this.data = data;
		this.checksum = BinaryResource.NEW_ENTITY;		
	}

	BinaryEntityImpl(BinaryResourceItem item) {
		this.key = item.getKey();
		this.data = item.getBytes();
		this.checksum = item.getChecksum();
	}
	
	BinaryEntityImpl(BinaryResourceItem item, byte[] data) {
		this.key = item.getKey();
		this.data = data;
		this.checksum = item.getChecksum();
	}
	
	public boolean isNew() {
		return checksum == BinaryResource.NEW_ENTITY;
	}
	public boolean isDeleted() {
		return checksum == BinaryResource.DELETED;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(data);
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BinaryEntityImpl other = (BinaryEntityImpl) obj;
		if (!Arrays.equals(data, other.data))
			return false;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		return true;
	}


}
