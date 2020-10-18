package org.k2.resource.entity;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.zip.Checksum;

import org.k2.resource.MetaResource;
import org.k2.resource.Resource;
import org.k2.resource.binary.BinaryResource;
import org.k2.resource.binary.exception.BinaryResourceInitializeException;
import org.k2.resource.exception.DuplicateKeyError;
import org.k2.resource.exception.MissingKeyError;
import org.k2.resource.exception.MutatingEntityError;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import lombok.Getter;

public class EntityResource<K,E> implements Resource<K,E> {
	
	private final BinaryResource resource;
	
	private final ObjectMapper defaultMetaMapper() {
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		SimpleModule module = new SimpleModule();
		module.addDeserializer(MetaResource.class, new MetaEntityResourceDeserializer());
		mapper.registerModule(module);
		return mapper;
	}

	public EntityResource(
			Class<K> keyType, 
			Class<E> entityType,
			File dir, 
			ThreadLocal<Checksum> checksum,
			EntitySerializationFactory<K,E> serializationFactory) throws BinaryResourceInitializeException {
		
		this.resource = new BinaryResource(
				dir, 
				checksum, 
				serializationFactory.create(keyType, entityType),
				defaultMetaMapper());
		if (!( resource.getMetaData() instanceof MetaEntityResource)) 
			throw new BinaryResourceInitializeException("Resource meta data is not applicable to an Entity resource", dir);
		if (!((MetaEntityResource)resource.getMetaData()).getKeyType().equals(keyType)) 
			throw new BinaryResourceInitializeException("Resource meta data key type missmatch", dir);
		if (!((MetaEntityResource)resource.getMetaData()).getEntityType().equals(entityType)) 
			throw new BinaryResourceInitializeException("Resource meta data entity type missmatch", dir);
		
		
	}

	@Override
	public E create(K key, E obj) throws DuplicateKeyError, MutatingEntityError {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public E get(K key) throws MissingKeyError {
		// TODO Auto-generated method stub
		return null;
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
