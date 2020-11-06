package org.k2.resource.location;

import java.io.File;

import org.k2.resource.exception.EntityLockedError;
import org.k2.resource.exception.ResourceConfigurationException;
import org.k2.resource.location.DigestableLocation;
import org.k2.resource.transaction.ResourceTransactionManager;
import org.k2.resource.transaction.TransactionManager;

public interface DigestableResource {

	static DigestableResource create(
			File location) throws ResourceConfigurationException {
		return DigestableResource.create(location, DigestableLocation.defaultDigestor());
	}

	static DigestableResource create(
			File location, 
			DigestableLocation.Digestor digestor) throws ResourceConfigurationException {
		return new SimpleDigestableResource(location, digestor);
	}
	
	static TxDigestableResource create(
			File location, 
			ResourceTransactionManager txManager) throws ResourceConfigurationException {
		return DigestableResource.create(location, DigestableLocation.defaultDigestor(), txManager);
	}

	static TxDigestableResource create(
			File location, 
			DigestableLocation.Digestor digestor, 
			ResourceTransactionManager txManager) throws ResourceConfigurationException {
		return new TxDigestableResource(location, digestor, txManager);
	}
	
	String getKey();
	byte[] getData();
	void setData(byte[] data) throws EntityLockedError;
	byte[] getDigest();
	String getChecksum();
	File getDatafile();
	void lock();
	void unlock();
	
}
