package org.k2.resource.binary;

public interface BinaryWrapper {
	
	String getKey();
	byte[] getData();
	long getChecksum();
	void setKey(String key);
	void setData(byte[] data);
	boolean isNew();
	boolean isDeleted();

}
