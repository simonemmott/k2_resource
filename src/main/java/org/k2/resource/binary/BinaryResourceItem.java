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
	
	private final File datafile;
	@Getter
	private long checksum;

	public BinaryResourceItem(File datafile, Checksum checksum) {
		assert datafile.isFile();
		assert datafile.exists();
		assert datafile.canRead();
		assert datafile.canWrite();
		
		this.datafile = datafile;
		byte[] data = getBytes();
		checksum.update(data, 0, data.length);
		this.checksum = checksum.getValue();
	}
	
	public String getKey() {
		return datafile.getName().split("\\.(?=[^\\.]+$)")[0];
	}
	
	byte[] getBytes() {
		try {
			return FileUtils.readFileToByteArray(datafile);
		} catch (IOException err) {
			throw new UnexpectedResourceError(
					MessageFormat.format("Unable to read bytes for binary resource item: {0}", datafile.getName()),
					err);
		}
	}
	
	void update(BinaryEntity entity, Checksum checksum) throws MutatingEntityError {
		if (entity.getChecksum() == this.checksum) {
			try {
				FileUtils.writeByteArrayToFile(datafile, entity.getData());
				checksum.update(entity.getData(), 0, entity.getData().length);
				this.checksum = checksum.getValue();
			} catch (IOException err) {
				throw new UnexpectedResourceError(
						MessageFormat.format("Unable to write bytes for binary resource item: {0}", datafile.getName()),
						err);
			}
		} else {
			throw new MutatingEntityError(BinaryResourceItem.class, entity.getKey());
		}
	}

}
