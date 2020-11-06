package org.k2.resource.location;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.k2.resource.exception.EntityLockedError;
import org.k2.resource.exception.ResourceConfigurationException;
import org.k2.resource.exception.UnexpectedResourceError;
import org.k2.resource.location.DigestableLocation.Digestor;
import org.k2.resource.transaction.ResourceTransaction;
import org.k2.resource.transaction.ResourceTransactionManager;
import org.k2.util.binary.BinaryUtils;

public class TxDigestableResource extends SimpleDigestableResource {
	
	private final ResourceTransactionManager txManager;
	private ResourceTransaction inTx;
	protected byte[] txDigest;
	protected String txChecksum;
	protected boolean txDigested = false;

	public TxDigestableResource(
			File location, 
			Digestor digestor, 
			ResourceTransactionManager txManager) throws ResourceConfigurationException {
		super(location, digestor);
		this.txManager = txManager;
	}
	
	@Override
	public byte[] getData() {
		try {
			super.lock();
			if (inTx != null) {
				if (txManager.hasTransaction() && inTx == txManager.getTransaction()) {
					return inTx.getTransactionData(this);
				} else {
					return inTx.getCommittedData(this);
				}
			}
			byte[] data = FileUtils.readFileToByteArray(super.getDatafile());
			digest(data);
			return data;
		} catch (IOException e) {
			throw new UnexpectedResourceError("Unable to read from datafile: "+super.getDatafile().getAbsolutePath(), e);
		} finally {
			super.unlock();
		}
	}

	@Override
	public void setData(byte[] data) throws EntityLockedError {
		try {
			super.lock();
			if (inTx == null) {
				inTx = txManager.getTransaction();
			} else if (inTx != txManager.getTransaction()) {
				throw new EntityLockedError(getKey(), "Entity is locked by another transaction");
			}
			inTx.put(this, data);
			digest(data);
		} finally {
			super.unlock();
		}
	}

	@Override
	protected void digest(byte[] data) {
		if (inTx == null) {
			super.digest(data);
		} else {
			txDigest = digestor.digest(data);
			txChecksum = BinaryUtils.hex(digest);
			txDigested = true;
		}
	}
	
	@Override
	public byte[] getDigest() {
		if (inTx == null) {
			return super.getDigest();
		} else {
			if (txDigested) return txDigest;
			digest(getData());
			return txDigest;
		}
	}

	@Override
	public String getChecksum() {
		if (inTx == null) {
			return super.getChecksum();
		} else {
			if (txDigested) return txChecksum;
			digest(getData());
			return txChecksum;
		}
	}

	public void release() {
		inTx = null;
	}



}
