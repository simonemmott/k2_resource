package org.k2.resource.entity;

import java.io.File;
import java.util.List;
import java.util.Set;
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
import org.k2.resource.exception.MissingKeyError;
import org.k2.resource.exception.MutatingEntityError;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import lombok.Getter;

public abstract class AbstractEntityResource<K,E> implements Resource<K,E> {
	
	private final BinaryResource resource;
	private final Class<K> keyType;
	private final Class<E> entityType;
	private final EntitySerialization<K,E> entitySerialization;
	
	private final ObjectMapper defaultMetaMapper() {
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		SimpleModule module = new SimpleModule();
		module.addDeserializer(MetaResource.class, new MetaEntityResourceDeserializer());
		mapper.registerModule(module);
		return mapper;
	}

	public AbstractEntityResource(
			Class<K> keyType, 
			Class<E> entityType,
			File dir) throws BinaryResourceInitializeException, EntityConfigurationException {
		this.entityType = entityType;
		this.keyType = keyType;
		this.entitySerialization = (EntitySerialization<K,E>) new DefaultEntitySerializationFactory(keyType, entityType)
				.create(keyType, entityType);
		this.resource = new BinaryResource(
				dir, 
				new ThreadLocal<Checksum>(), 
				this.entitySerialization,
				defaultMetaMapper());
		if (!( resource.getMetaData() instanceof MetaEntityResource)) 
			throw new BinaryResourceInitializeException("Resource meta data is not applicable to an Entity resource", dir);
		if (!((MetaEntityResource)resource.getMetaData()).getKeyType().equals(keyType)) 
			throw new BinaryResourceInitializeException("Resource meta data key type missmatch", dir);
		if (!((MetaEntityResource)resource.getMetaData()).getEntityType().equals(entityType)) 
			throw new BinaryResourceInitializeException("Resource meta data entity type missmatch", dir);
	}

	public AbstractEntityResource(
			Class<K> keyType, 
			Class<E> entityType,
			File dir, 
			ThreadLocal<Checksum> checksum) throws BinaryResourceInitializeException, EntityConfigurationException {
		
		this.entityType = entityType;
		this.keyType = keyType;
		this.entitySerialization = (EntitySerialization<K,E>) new DefaultEntitySerializationFactory<K,E>(keyType, entityType)
				.create(keyType, entityType);
		this.resource = new BinaryResource(
				dir, 
				checksum, 
				this.entitySerialization,
				defaultMetaMapper());
		if (!( resource.getMetaData() instanceof MetaEntityResource)) 
			throw new BinaryResourceInitializeException("Resource meta data is not applicable to an Entity resource", dir);
		if (!((MetaEntityResource)resource.getMetaData()).getKeyType().equals(keyType)) {
			System.out.println(((MetaEntityResource)resource.getMetaData()).getKeyType());
			throw new BinaryResourceInitializeException("Resource meta data key type missmatch", dir);
		}
		if (!((MetaEntityResource)resource.getMetaData()).getEntityType().equals(entityType)) 
			throw new BinaryResourceInitializeException("Resource meta data entity type missmatch", dir);
	}

	public AbstractEntityResource(
			Class<K> keyType, 
			Class<E> entityType,
			File dir, 
			ThreadLocal<Checksum> checksum,
			EntitySerializationFactory<K,E> serializationFactory) throws BinaryResourceInitializeException {
		
		this.entityType = entityType;
		this.keyType = keyType;
		this.entitySerialization = (EntitySerialization<K,E>) serializationFactory.create(keyType, entityType);
		this.resource = new BinaryResource(
				dir, 
				checksum, 
				this.entitySerialization,
				defaultMetaMapper());
		if (!( resource.getMetaData() instanceof MetaEntityResource)) 
			throw new BinaryResourceInitializeException("Resource meta data is not applicable to an Entity resource", dir);
		if (!((MetaEntityResource)resource.getMetaData()).getKeyType().equals(keyType)) 
			throw new BinaryResourceInitializeException("Resource meta data key type missmatch", dir);
		if (!((MetaEntityResource)resource.getMetaData()).getEntityType().equals(entityType)) 
			throw new BinaryResourceInitializeException("Resource meta data entity type missmatch", dir);
		
		
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
		BinaryEntity be = resource.create(keyStr, new BinaryEntityImpl(keyStr, entitySerialization.getSerializer().serialize(obj)));
		sess.put(entityType, key, obj, be.getChecksum());
		return obj;
	}

	@Override
	public E get(K key) throws MissingKeyError {
		ResourceSession sess = getSession();
		if (sess.has(entityType, key)) {
			return sess.get(entityType, key);
		}
		BinaryEntity be = resource.get(entitySerialization.getKeySerializer().serialize(key));
		E obj = entitySerialization.getDeserializer().deserialize(be.getData());
		sess.put(entityType, key, obj, be.getChecksum());
		return obj;
	}

	@Override
	public E update(K key, E obj) throws MissingKeyError, MutatingEntityError {
		ResourceSession sess = getSession();
		if (sess.isDeleted(entityType, key)) {
			throw new MutatingEntityError("Unable to update an object which has already been deleted");
		}
		long checksum = sess.checksum(entityType, key);
		String keyStr = entitySerialization.getKeySerializer().serialize(key);
		BinaryEntity be = resource.update(keyStr, new BinaryEntityImpl(keyStr, entitySerialization.getSerializer().serialize(obj), checksum));
		sess.put(entityType, key, obj, be.getChecksum());
		return obj;
	}

	@Override
	public E save(E obj) throws MissingKeyError, MutatingEntityError, DuplicateKeyError {
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
		List<E> items = sess.fetch(entityType);
		for (BinaryEntity be : resource.fetch()) {
			E obj = entitySerialization.getDeserializer().deserialize(be.getData());
			K key = entitySerialization.getKeyGetter().get(obj);
			if (!items.contains(obj) && !sess.isDeleted(entityType, key)) {
				items.add(obj);
				sess.put(entityType, key, obj, be.getChecksum());				
			}
		}
		return items;
	}

	@Override
	public E remove(K key) throws MissingKeyError, MutatingEntityError {
		ResourceSession sess = getSession();
		BinaryEntity be = resource.remove(entitySerialization.getKeySerializer().serialize(key));
		E obj = entitySerialization.getDeserializer().deserialize(be.getData());
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
		return resource.exists(entitySerialization.getKeySerializer().serialize(key));
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
