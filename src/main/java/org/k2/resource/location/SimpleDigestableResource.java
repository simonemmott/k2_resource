package org.k2.resource.location;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.k2.resource.exception.ResourceConfigurationException;
import org.k2.resource.exception.UnexpectedResourceError;
import org.k2.resource.location.DigestableLocation.Digestor;
import org.k2.util.binary.BinaryUtils;

public class SimpleDigestableResource implements DigestableResource {

	private final File location;
	private final Digestor digestor;
	private byte[] digest;
	private String checksum;
	private boolean digested = false;

	public SimpleDigestableResource(File location, Digestor digestor) throws ResourceConfigurationException {
		if (! location.exists()) throw new ResourceConfigurationException(location, "does not exist!");
		if (! location.isFile()) throw new ResourceConfigurationException(location, "is not a file!");
		if (! location.canRead()) throw new ResourceConfigurationException(location, "cannot be read!");
		if (! location.canWrite()) throw new ResourceConfigurationException(location, "cannot be written!");	
		this.location = location;
		this.digestor = digestor;
	}

	@Override
	public String getKey() {
		return location.getName().split("\\.(?=[^\\.]+$)")[0];
	}

	@Override
	public byte[] getData() {
		byte[] data;
		try {
			data = FileUtils.readFileToByteArray(location);
		} catch (IOException e) {
			throw new UnexpectedResourceError("Unable to read from datafile: "+location.getAbsolutePath(), e);
		}
		digest(data);
		return data;
	}

	@Override
	public void setData(byte[] data) {
		try {
			FileUtils.writeByteArrayToFile(location, data);
		} catch (IOException e) {
			throw new UnexpectedResourceError("Unable to write to datafile: "+location.getAbsolutePath(), e);
		}
		digest(data);
	}
	
	private void digest(byte[] data) {
		digest = digestor.digest(data);
		checksum = BinaryUtils.hex(digest);
		digested = true;
	}

	private void digest() {
		digest = digestor.digest(getData());
		checksum = BinaryUtils.hex(digest);
		digested = true;
	}

	@Override
	public byte[] getDigest() {
		if (digested) return digest;
		digest();
		return digest;
	}

	@Override
	public String getChecksum() {
		if (digested) return checksum;
		digest();
		return checksum;
	}

	@Override
	public File getDatafile() {
		return location;
	}

	@Override
	public void lock() {
		//TODO
		throw new RuntimeException("NOT_IMPLEMENTED");
		
	}

	@Override
	public void unlock() {
		//TODO
		throw new RuntimeException("NOT_IMPLEMENTED");
		
	}

}
