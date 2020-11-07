package org.k2.resource.location;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Set;

import org.k2.resource.MetaResource;
import org.k2.resource.entity.EntityResourceManager;
import org.k2.resource.exception.DuplicateKeyError;
import org.k2.resource.exception.MissingKeyError;
import org.k2.resource.exception.ResourceConfigurationException;
import org.k2.resource.exception.UnexpectedResourceError;
import org.k2.resource.transaction.ResourceTransactionManager;

public interface DigestableLocation {
	
	interface Digestor {
		byte[] digest(byte[] data);
	}
	
	static Digestor defaultDigestor() {
		ThreadLocal<MessageDigest> digest = ThreadLocal.withInitial(() -> {
			try {
				return MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException e) {
				throw new UnexpectedResourceError("Unable to create MD5 digest");
			}
		});
		return (byte[] data) -> {
			MessageDigest md = digest.get();
			md.update(data);
			return md.digest();
		};
	}
	
	static DigestableLocation create(
			File location) throws ResourceConfigurationException {
		return DigestableLocation.create(location, defaultDigestor());
	}

	static DigestableLocation create(
			File location, 
			Digestor digestor) throws ResourceConfigurationException {
		return new SimpleDigestableLocation(location, digestor);
	}
	
	static TxDigestableLocation create(
			File location, 
			EntityResourceManager resManager) throws ResourceConfigurationException {
		return DigestableLocation.create(location, defaultDigestor(), resManager);
	}

	static TxDigestableLocation create(
			File location, 
			Digestor digestor, 
			EntityResourceManager resManager) throws ResourceConfigurationException {
		return new TxDigestableLocation(location, digestor, resManager);
	}
	
	String getName();
	boolean hasMetadata();
	<M extends MetaResource> M getMetadata(Class<M> metadataType);
	<M extends MetaResource> void setMetadata(M metadata);
	Set<String> keys();
	DigestableResource getResource(String key) throws MissingKeyError;
	DigestableResource createResource(String key) throws DuplicateKeyError;
	DigestableResource removeResource(String key) throws MissingKeyError;
	boolean resourceExists(String key);
	Set<String> locations();
	DigestableLocation getLocation(String name) throws MissingKeyError;
	DigestableLocation createLocation(String name) throws DuplicateKeyError;
	<M extends MetaResource> DigestableLocation createLocation(String name, M metadata) throws DuplicateKeyError;
	DigestableLocation removeLocation(String name) throws MissingKeyError;
	boolean locationExists(String name);
	void refresh();
	void digest() throws ResourceConfigurationException;
	File getLocation();
	void clean();
	Collection<? extends DigestableResource> getResources();

}
