package org.k2.resource.entity.simple;

import java.io.File;
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

	public SimpleEntityResource(Class<K> keyType, Class<E> entityType, File dir, ThreadLocal<Checksum> checksum) throws BinaryResourceInitializeException, EntityConfigurationException {
		super(keyType, entityType, dir, checksum);
		this.session = new SimpleResourceSession();
	}

	public SimpleEntityResource(Class<K> keyType, Class<E> entityType, File dir, ThreadLocal<Checksum> checksum,
			ResourceSession session) throws BinaryResourceInitializeException, EntityConfigurationException {
		super(keyType, entityType, dir, checksum);
		this.session = session;
	}

	public SimpleEntityResource(Class<K> keyType, Class<E> entityType, File dir, ThreadLocal<Checksum> checksum,
			EntitySerializationFactory<K, E> serializationFactory, ResourceSession session) throws BinaryResourceInitializeException {
		super(keyType, entityType, dir, checksum, serializationFactory);
		this.session = session;
	}

	public SimpleEntityResource(Class<K> keyType, Class<E> entityType, File dir, ThreadLocal<Checksum> checksum,
			EntitySerializationFactory<K, E> serializationFactory) throws BinaryResourceInitializeException {
		super(keyType, entityType, dir, checksum, serializationFactory);
		this.session = new SimpleResourceSession();
	}

	protected ResourceSession getSession() {
		return session;
	}

}
