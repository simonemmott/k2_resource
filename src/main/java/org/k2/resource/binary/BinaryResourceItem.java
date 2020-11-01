package org.k2.resource.binary;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.text.MessageFormat;
import java.util.zip.Checksum;

import org.apache.commons.io.FileUtils;
import org.k2.resource.exception.MutatingEntityError;
import org.k2.resource.exception.UnexpectedResourceError;
import org.k2.util.binary.BinaryUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Getter;

public class BinaryResourceItem {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(BinaryResourceItem.class);
	
	private final BinaryResource resource;
	private final File datafile;
	@Getter
	private String checksum;
	private byte[] data;
	
	public BinaryResourceItem(
			BinaryResource resource, 
			BinaryEntity obj, 
			MessageDigest digest) throws IOException {
		this.resource = resource;
		this.datafile = new File(
				resource.getDir().getPath()+File.separatorChar+
				obj.getKey()+"."+resource.getDatafileExtension());
		this.data = obj.getData();
		digest.update(data);
		this.checksum = BinaryUtils.hex(digest.digest());
		FileUtils.writeByteArrayToFile(datafile, data);
	}

	public BinaryResourceItem(
			BinaryResource resource, 
			File datafile, 
			MessageDigest digest) throws IOException {
		this.resource = resource;
		this.datafile = datafile;
		this.data = FileUtils.readFileToByteArray(datafile);
		digest.update(data);
		this.checksum = BinaryUtils.hex(digest.digest());
	}
	
	boolean isNew() {
		return checksum == BinaryResource.NEW_ENTITY;
	}
	boolean isDeleted() {
		return checksum == BinaryResource.DELETED;
	}

	public String getKey() {
		return datafile.getName().split("\\.(?=[^\\.]+$)")[0];
	}
	
	byte[] getBytes() {
		return data;
	}
	
	void update(BinaryEntity obj, MessageDigest digest) throws MutatingEntityError, IOException {
		if (isDeleted()) throw new MutatingEntityError(getKey(), "Unable to update a deleted binary item");
		if (obj.getChecksum() == this.checksum) {
			data = obj.getData();
			FileUtils.writeByteArrayToFile(datafile, data);
			digest.update(data);
			this.checksum = BinaryUtils.hex(digest.digest());
		} else {
			throw new MutatingEntityError(obj.getKey());
		}
	}
	
	byte[] delete() throws MutatingEntityError, IOException {
		if (isDeleted()) throw new MutatingEntityError(getKey(), "Unable to delete a deleted binary item");
		datafile.delete();
		checksum = BinaryResource.DELETED;
		return data;
	}
	
}
