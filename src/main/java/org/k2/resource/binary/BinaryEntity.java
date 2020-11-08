package org.k2.resource.binary;

public interface BinaryEntity {
	
	String getKey();
	byte[] getData();
	String getChecksum();
	void setKey(String key);
	void setData(byte[] data);
	boolean isNew();
	boolean isDeleted();

}
