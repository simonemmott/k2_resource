package org.k2.resource.entity;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.Checksum;

import org.k2.resource.KeyGenerator;
import org.k2.resource.MetaResource;
import org.k2.resource.Resource;
import org.k2.resource.binary.BinaryEntity;
import org.k2.resource.binary.BinaryEntityImpl;
import org.k2.resource.binary.BinaryResource;
import org.k2.resource.binary.exception.BinaryResourceInitializeException;
import org.k2.resource.entity.exception.EntityConfigurationException;
import org.k2.resource.entity.serialize.DefaultEntitySerializationFactory;
import org.k2.resource.entity.serialize.EntitySerialization;
import org.k2.resource.entity.serialize.EntitySerializationFactory;
import org.k2.resource.exception.DuplicateKeyError;
import org.k2.resource.exception.EntityLockedError;
import org.k2.resource.exception.MissingKeyError;
import org.k2.resource.exception.MutatingEntityError;
import org.k2.resource.exception.UnexpectedResourceError;
import org.k2.resource.location.TxDigestableLocation;
import org.k2.resource.location.TxDigestableResource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import lombok.Getter;

public abstract class ManagedEntityResource<K,E> implements Resource<K,E> {
	
	private final TxDigestableLocation resource;
	private final Class<K> keyType;
	private final Class<E> entityType;
	private EntitySerialization<K,E> entitySerialization;
	
	private final ObjectMapper defaultMetaMapper() {
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		SimpleModule module = new SimpleModule();
		module.addDeserializer(MetaResource.class, new MetaEntityResourceDeserializer());
		mapper.registerModule(module);
		return mapper;
	}

	public ManagedEntityResource(
			Class<K> keyType, 
			Class<E> entityType,
			TxDigestableLocation resource) throws BinaryResourceInitializeException, EntityConfigurationException {
		this.entityType = entityType;
		this.keyType = keyType;
		this.resource = resource;
		if (this.resource.hasMetadata()) {
			MetaEntityResource metaData = this.resource.getMetadata(MetaEntityResource.class);
			this.entitySerialization = (EntitySerialization<K,E>) new DefaultEntitySerializationFactory(keyType, entityType, metaData)
				.create(keyType, entityType);
		}
	}

	public ManagedEntityResource(
			Class<K> keyType, 
			Class<E> entityType,
			TxDigestableLocation resource, 
			ThreadLocal<MessageDigest> digest) throws BinaryResourceInitializeException, EntityConfigurationException {
		
		this.entityType = entityType;
		this.keyType = keyType;
		this.resource = resource;
		if (this.resource.hasMetadata()) {
			MetaEntityResource metaData = this.resource.getMetadata(MetaEntityResource.class);
			this.entitySerialization = (EntitySerialization<K,E>) new DefaultEntitySerializationFactory<K,E>(keyType, entityType, metaData)
				.create(keyType, entityType);
		}
	}

	public ManagedEntityResource(
			Class<K> keyType, 
			Class<E> entityType,
			TxDigestableLocation resource, 
			ThreadLocal<MessageDigest> digest,
			EntitySerializationFactory<K,E> serializationFactory) throws BinaryResourceInitializeException {
		
		this.entityType = entityType;
		this.keyType = keyType;
		this.entitySerialization = (EntitySerialization<K,E>) serializationFactory.create(keyType, entityType);
		this.resource = resource;		
	}
	
	protected abstract ResourceSession getSession();

	@Override
	public E create(K key, E obj) throws DuplicateKeyError, MutatingEntityError {
		ResourceSession sess = getSession();
		entitySerialization.getKeySetter().set(obj, key);
		String keyStr = entitySerialization.getKeySerializer().serialize(key);
		if (sess.has(entityType, key)) {
			throw new DuplicateKeyError(keyStr);
		}
		TxDigestableResource objResource = resource.createResource(keyStr);
		try {
			objResource.setData(entitySerialization.getSerializer().serialize(obj));
		} catch (EntityLockedError e) {
			throw new MutatingEntityError(keyStr, "Unable to serialize new entity");
		}
		sess.put(entityType, key, obj, objResource.getChecksum());
		return obj;
	}

	@Override
	public E get(K key) throws MissingKeyError {
		ResourceSession sess = getSession();
		if (sess.has(entityType, key)) {
			return sess.get(entityType, key);
		}
		TxDigestableResource objResource = resource.getResource(entitySerialization.getKeySerializer().serialize(key));
		E obj = entitySerialization.getDeserializer().deserialize(objResource.getData());
		sess.put(entityType, key, obj, objResource.getChecksum());
		return obj;
	}

	@Override
	public E update(K key, E obj) throws MissingKeyError, MutatingEntityError, EntityLockedError {
		ResourceSession sess = getSession();
		if (sess.isDeleted(entityType, key)) {
			throw new MutatingEntityError("Unable to update an object which has already been deleted");
		}
		String checksum = sess.checksum(entityType, key);
		String keyStr = entitySerialization.getKeySerializer().serialize(key);
		TxDigestableResource objResource = resource.getResource(keyStr);
		if (objResource.getChecksum().equals(checksum)) {
			objResource.setData(entitySerialization.getSerializer().serialize(obj));
			sess.put(entityType, key, obj, objResource.getChecksum());
			return obj;
		}
		throw new MutatingEntityError("Unable to update an object which has already been updated elsewhere");
	}

	@Override
	public E save(E obj) throws MissingKeyError, MutatingEntityError, DuplicateKeyError, EntityLockedError {
		ResourceSession sess = getSession();
		K key = entitySerialization.getKeyGetter().get(obj);
		if (key == null) {
			if (entitySerialization.getKeyGenerator() != null) {
				key = entitySerialization.getKeyGenerator().generate();
				entitySerialization.getKeySetter().set(obj, key);
			} else {
				throw new MissingKeyError("No key and no key generator");
			}
		}
		if (sess.has(entityType, key)) {
			return update(key, obj);
		} else {
			return create(key, obj);
		}
	}

	@Override
	public List<E> fetch() {
		ResourceSession sess = getSession();
//		Set<E> cachedSet = sess.fetch(entityType);
		List<E> items = sess.fetch(entityType).stream().collect(Collectors.toList());
		for (TxDigestableResource objResource : resource.getResources()) {
			E obj = entitySerialization.getDeserializer().deserialize(objResource.getData());
			K key = entitySerialization.getKeyGetter().get(obj);
			if (!items.contains(obj) && !sess.isDeleted(entityType, key)) {
				items.add(obj);
				sess.put(entityType, key, obj, objResource.getChecksum());				
			}
		}
		return items;
	}

	@Override
	public E remove(K key) throws MissingKeyError, MutatingEntityError {
		ResourceSession sess = getSession();
		TxDigestableResource objResource = resource.removeResource(entitySerialization.getKeySerializer().serialize(key));
		E obj = entitySerialization.getDeserializer().deserialize(objResource.getData());
		if (sess.has(entityType, key)) {
			sess.delete(entityType, key);
		}
		return obj;
	}

	@Override
	public void delete(E obj) throws MissingKeyError, MutatingEntityError {
		ResourceSession sess = getSession();
		K key = entitySerialization.getKeyGetter().get(obj);
		if (key == null) {
			throw new MissingKeyError("No key during delete");
		}
		remove(key);
	}

	@Override
	public int count() {
		return keys().size();
	}

	@Override
	public boolean exists(K key) {
		ResourceSession sess = getSession();
		if (sess.has(entityType, key)) {
			if (sess.isDeleted(entityType, key)) return false;
			return true;
		}
		return resource.resourceExists(entitySerialization.getKeySerializer().serialize(key));
	}

	@Override
	public Set<K> keys() {
		ResourceSession sess = getSession();
		Set<K> keys = sess.keys(entityType, keyType);
		for (String keyStr : resource.keys()) {
			K key = entitySerialization.getKeyDeserializer().deserialize(keyStr);
			if (!keys.contains(key)) {
				keys.add(key);
			}
		}
		return keys;
	}

}
