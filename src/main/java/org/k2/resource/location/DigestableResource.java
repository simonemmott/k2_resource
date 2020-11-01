package org.k2.resource.location;

import java.io.File;

import org.k2.resource.exception.ResourceConfigurationException;
import org.k2.resource.location.DigestableLocation;

public interface DigestableResource {

	static DigestableResource create(File location) throws ResourceConfigurationException {
		return DigestableResource.create(location, DigestableLocation.defaultDigestor());
	}

	static DigestableResource create(File location, DigestableLocation.Digestor digestor) throws ResourceConfigurationException {
		return new SimpleDigestableResource(location, digestor);
	}
	
	String getKey();
	byte[] getData();
	void setData(byte[] data);
	byte[] getDigest();
	String getChecksum();
	File getDatafile();
	
}
