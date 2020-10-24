package org.k2.resource.entity.serialize;

import java.io.IOException;

import org.k2.resource.entity.exception.UnexpectedDeserializationError;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

public class DefaultEntityDeserializer<E> implements EntityDeserializer<E> {

	private final Class<E> entityType;
	private final ObjectReader reader;
	
	public DefaultEntityDeserializer (Class<E> entityType) {
		this.entityType = entityType;
		this.reader = new ObjectMapper().readerFor(entityType);
	}

	@Override
	public E deserialize(byte[] data) {
		try {
			return reader.readValue(data);
		} catch (IOException e) {
			throw new UnexpectedDeserializationError(entityType, e);
		}
	}

}
