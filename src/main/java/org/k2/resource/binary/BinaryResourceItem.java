package org.k2.resource.binary;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.zip.Checksum;

import org.apache.commons.io.FileUtils;
import org.k2.resource.exception.MutatingEntityError;
import org.k2.resource.exception.UnexpectedResourceError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Getter;

public class BinaryResourceItem {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(BinaryResourceItem.class);
	
	private final BinaryResource resource;
	private final File datafile;
	@Getter
	private long checksum;
	private byte[] data;
	
	public BinaryResourceItem(
			BinaryResource resource, 
			BinaryEntity obj, 
			Checksum checksum) throws IOException {
		this.resource = resource;
		this.datafile = new File(
				resource.getDir().getPath()+File.separatorChar+
				obj.getKey()+"."+resource.getDatafileExtension());
		this.data = obj.getData();
		checksum.update(data, 0, data.length);
		this.checksum = checksum.getValue();
		FileUtils.writeByteArrayToFile(datafile, data);
	}

	public BinaryResourceItem(
			BinaryResource resource, 
			File datafile, 
			Checksum checksum) throws IOException {
		this.resource = resource;
		this.datafile = datafile;
		this.data = FileUtils.readFileToByteArray(datafile);
		checksum.update(data, 0, data.length);
		this.checksum = checksum.getValue();
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
	
	void update(BinaryEntity obj, Checksum checksum) throws MutatingEntityError, IOException {
		if (isDeleted()) throw new MutatingEntityError(getKey(), "Unable to update a deleted binary item");
		if (obj.getChecksum() == this.checksum) {
			data = obj.getData();
			FileUtils.writeByteArrayToFile(datafile, data);
			checksum.update(data, 0, data.length);
			this.checksum = checksum.getValue();
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
