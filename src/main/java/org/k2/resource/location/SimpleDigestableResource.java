package org.k2.resource.location;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.io.FileUtils;
import org.k2.resource.exception.EntityLockedError;
import org.k2.resource.exception.ResourceConfigurationException;
import org.k2.resource.exception.UnexpectedResourceError;
import org.k2.resource.location.DigestableLocation.Digestor;
import org.k2.util.binary.BinaryUtils;

public class SimpleDigestableResource implements DigestableResource {

	private final File location;
	protected final Digestor digestor;
	protected byte[] digest;
	protected String checksum;
	protected boolean digested = false;
	private ReadWriteLock lock = new ReentrantReadWriteLock();
	private Lock writeLock = lock.writeLock();
	private Lock readLock = lock.readLock();

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
		try {
			readLock.lock();
			byte[] data = FileUtils.readFileToByteArray(location);
			digest(data);
			return data;
		} catch (IOException e) {
			throw new UnexpectedResourceError("Unable to read from datafile: "+location.getAbsolutePath(), e);
		} finally {
			readLock.unlock();
		}
	}

	@Override
	public void setData(byte[] data) throws EntityLockedError {
		try {
			writeLock.lock();
			FileUtils.writeByteArrayToFile(location, data);
			digest(data);
		} catch (IOException e) {
			throw new UnexpectedResourceError("Unable to write to datafile: "+location.getAbsolutePath(), e);
		} finally {
			writeLock.unlock();
		}
	}
	
	protected void digest(byte[] data) {
		digest = digestor.digest(data);
		checksum = BinaryUtils.hex(digest);
		digested = true;
	}

	@Override
	public byte[] getDigest() {
		if (digested) return digest;
		digest(getData());
		return digest;
	}

	@Override
	public String getChecksum() {
		if (digested) return checksum;
		digest(getData());
		return checksum;
	}

	@Override
	public File getDatafile() {
		return location;
	}

	@Override
	public void lock() {
		writeLock.lock();
		
	}

	@Override
	public void unlock() {
		writeLock.unlock();
		
	}

}
