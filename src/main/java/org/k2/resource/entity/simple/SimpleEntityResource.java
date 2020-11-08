package org.k2.resource.entity.simple;

import java.io.File;
import java.security.MessageDigest;
import java.util.zip.Checksum;

import org.k2.resource.binary.exception.BinaryResourceInitializeException;
import org.k2.resource.entity.AbstractEntityResource;
import org.k2.resource.entity.ResourceSession;
import org.k2.resource.entity.exception.EntityConfigurationException;
import org.k2.resource.entity.serialize.EntitySerializationFactory;

public class SimpleEntityResource<K,E> extends AbstractEntityResource<K,E> {
	
	private final ResourceSession session;

	public SimpleEntityResource(Class<K> keyType, Class<E> entityType, File dir) throws BinaryResourceInitializeException, EntityConfigurationException {
		super(keyType, entityType, dir);
		this.session = new SimpleResourceSession();
	}

	public SimpleEntityResource(Class<K> keyType, Class<E> entityType, File dir, ResourceSession session) throws BinaryResourceInitializeException, EntityConfigurationException {
		super(keyType, entityType, dir);
		this.session = session;
	}

	public SimpleEntityResource(Class<K> keyType, Class<E> entityType, File dir, ThreadLocal<MessageDigest> digest) throws BinaryResourceInitializeException, EntityConfigurationException {
		super(keyType, entityType, dir, digest);
		this.session = new SimpleResourceSession();
	}

	public SimpleEntityResource(Class<K> keyType, Class<E> entityType, File dir, ThreadLocal<MessageDigest> digest,
			ResourceSession session) throws BinaryResourceInitializeException, EntityConfigurationException {
		super(keyType, entityType, dir, digest);
		this.session = session;
	}

	public SimpleEntityResource(Class<K> keyType, Class<E> entityType, File dir, ThreadLocal<MessageDigest> digest,
			EntitySerializationFactory<K, E> serializationFactory, ResourceSession session) throws BinaryResourceInitializeException {
		super(keyType, entityType, dir, digest, serializationFactory);
		this.session = session;
	}

	public SimpleEntityResource(Class<K> keyType, Class<E> entityType, File dir, ThreadLocal<MessageDigest> digest,
			EntitySerializationFactory<K, E> serializationFactory) throws BinaryResourceInitializeException {
		super(keyType, entityType, dir, digest, serializationFactory);
		this.session = new SimpleResourceSession();
	}

	protected ResourceSession getSession() {
		return session;
	}

}
