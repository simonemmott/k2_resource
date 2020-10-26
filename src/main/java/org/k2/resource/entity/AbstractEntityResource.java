package org.k2.resource.entity;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.zip.Checksum;

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
		if (!((MetaEntityResource)resource.getMetaData()).getKeyType().equals(keyType)) 
			throw new BinaryResourceInitializeException("Resource meta data key type missmatch", dir);
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public E save(E obj) throws MissingKeyError, MutatingEntityError, DuplicateKeyError {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<E> fetch() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public E remove(K key) throws MissingKeyError, MutatingEntityError {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete(E obj) throws MissingKeyError, MutatingEntityError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int count() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean exists(K key) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Set<K> keys() {
		// TODO Auto-generated method stub
		return null;
	}

}
