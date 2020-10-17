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
	
	public BinaryResourceItem(
			BinaryResource resource, 
			BinaryEntity entity, 
			Checksum checksum) throws IOException {
		this.resource = resource;
		this.datafile = new File(
				resource.getDir().getPath()+File.separatorChar+
				entity.getKey()+"."+resource.getDatafileExtension());
		checksum.update(entity.getData(), 0, entity.getData().length);
		this.checksum = checksum.getValue();
		FileUtils.writeByteArrayToFile(datafile, entity.getData());
	}

	public BinaryResourceItem(
			BinaryResource resource, 
			File datafile, 
			Checksum checksum) throws IOException {
		this.resource = resource;
		this.datafile = datafile;
		byte[] data = getBytes();
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
	
	byte[] getBytes() throws IOException {
		return FileUtils.readFileToByteArray(datafile);
	}
	
	void update(BinaryEntity entity, Checksum checksum) throws MutatingEntityError, IOException {
		if (isDeleted()) throw new MutatingEntityError(getKey(), "Unable to update a deleted binary item");
		if (entity.getChecksum() == this.checksum) {
			FileUtils.writeByteArrayToFile(datafile, entity.getData());
			checksum.update(entity.getData(), 0, entity.getData().length);
			this.checksum = checksum.getValue();
		} else {
			throw new MutatingEntityError(entity.getKey());
		}
	}
	
	byte[] delete() throws MutatingEntityError, IOException {
		if (isDeleted()) throw new MutatingEntityError(getKey(), "Unable to delete a deleted binary item");
		byte[] data = getBytes();
		datafile.delete();
		this.checksum = BinaryResource.DELETED;
		return data;
	}
	
}
