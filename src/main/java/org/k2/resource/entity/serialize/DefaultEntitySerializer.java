package org.k2.resource.entity.serialize;

import org.k2.resource.entity.exception.UnexpectedSerializationError;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class DefaultEntitySerializer<E> implements EntitySerializer<E> {

	private final Class<E> entityType;
	private final ObjectWriter writer;
	
	public DefaultEntitySerializer (Class<E> entityType) {
		this.entityType = entityType;
		this.writer = new ObjectMapper().writerFor(entityType);
	}

	@Override
	public byte[] serialize(E entity) {
		try {
			return writer.writeValueAsBytes(entity);
		} catch (JsonProcessingException e) {
			throw new UnexpectedSerializationError(entityType, e);
		}
	}

}
