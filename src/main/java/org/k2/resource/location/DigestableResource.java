package org.k2.resource.location;

import java.io.File;

import org.k2.resource.exception.EntityLockedError;
import org.k2.resource.exception.ResourceConfigurationException;
import org.k2.resource.location.DigestableLocation;
import org.k2.resource.transaction.ResourceTransactionManager;
import org.k2.resource.transaction.TransactionManager;

public interface DigestableResource {

	static DigestableResource create(
			DigestableLocation location,
			File datafile) throws ResourceConfigurationException {
		return DigestableResource.create(location, datafile, DigestableLocation.defaultDigestor());
	}

	static DigestableResource create(
			DigestableLocation location,
			File datafile, 
			DigestableLocation.Digestor digestor) throws ResourceConfigurationException {
		return new SimpleDigestableResource(location, datafile, digestor);
	}
	
	static TxDigestableResource create(
			TxDigestableLocation location,
			File datafile, 
			ResourceTransactionManager txManager) throws ResourceConfigurationException {
		return DigestableResource.create(location, datafile, DigestableLocation.defaultDigestor(), txManager);
	}

	static TxDigestableResource create(
			TxDigestableLocation location,
			File datafile, 
			DigestableLocation.Digestor digestor, 
			ResourceTransactionManager txManager) throws ResourceConfigurationException {
		return new TxDigestableResource(location, datafile, digestor, txManager);
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
