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

import lombok.Getter;

public class SimpleDigestableResource implements DigestableResource {

	@Getter
	private final DigestableLocation location;
	private final File datafile;
	protected final Digestor digestor;
	protected byte[] digest;
	protected String checksum;
	protected boolean digested = false;
	private ReadWriteLock lock = new ReentrantReadWriteLock();
	private Lock writeLock = lock.writeLock();
	private Lock readLock = lock.readLock();

	public SimpleDigestableResource(DigestableLocation location, File datafile, Digestor digestor) throws ResourceConfigurationException {
		this.location = location;
		this.datafile = datafile;
		this.digestor = digestor;
	}

	@Override
	public String getKey() {
		return datafile.getName().split("\\.(?=[^\\.]+$)")[0];
	}

	@Override
	public byte[] getData() {
		try {
			readLock.lock();
			byte[] data = FileUtils.readFileToByteArray(datafile);
			digest(data);
			return data;
		} catch (IOException e) {
			throw new UnexpectedResourceError("Unable to read from datafile: "+datafile.getAbsolutePath(), e);
		} finally {
			readLock.unlock();
		}
	}

	@Override
	public void setData(byte[] data) throws EntityLockedError {
		try {
			writeLock.lock();
			if (!datafile.exists())
				datafile.createNewFile();
			FileUtils.writeByteArrayToFile(datafile, data);
			digest(data);
		} catch (IOException e) {
			throw new UnexpectedResourceError("Unable to write to datafile: "+datafile.getAbsolutePath(), e);
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
		return datafile;
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
