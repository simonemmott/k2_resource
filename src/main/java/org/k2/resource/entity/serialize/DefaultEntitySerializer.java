package org.k2.resource.entity.serialize;

import org.k2.resource.entity.MetaEntityResource;
import org.k2.resource.entity.exception.UnexpectedSerializationError;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class DefaultEntitySerializer<E> implements EntitySerializer<E> {

	private final Class<E> entityType;
	private final ObjectWriter writer;
	
	public DefaultEntitySerializer (Class<E> entityType) {
		this.entityType = entityType;
		this.writer = new ObjectMapper().writerFor(entityType).withDefaultPrettyPrinter();
	}

	public DefaultEntitySerializer(Class<E> entityType, MetaEntityResource metaData) {
		this.entityType = entityType;
		if (metaData.getDatafileExtension().equalsIgnoreCase("json")) {
			if (metaData.isPrettyPrint()) {
				this.writer = new ObjectMapper().writerFor(entityType).withDefaultPrettyPrinter();
			} else {
				this.writer = new ObjectMapper().writerFor(entityType);
			}
		} else if (metaData.getDatafileExtension().equalsIgnoreCase("yml") || metaData.getDatafileExtension().equalsIgnoreCase("yaml")) {
			ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
			this.writer = new ObjectMapper(new YAMLFactory()).writerFor(entityType);
		} else {
			this.writer = new ObjectMapper().writerFor(entityType);
		}
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
