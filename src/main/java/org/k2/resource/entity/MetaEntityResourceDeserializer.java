package org.k2.resource.entity;

import java.io.IOException;

import org.k2.resource.MetaResource;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class MetaEntityResourceDeserializer extends StdDeserializer<MetaResource> {

	public MetaEntityResourceDeserializer() {
		super(MetaResource.class);
	}

	@Override
	public MetaEntityResource deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		MetaEntityResource meta = new MetaEntityResource();
		JsonNode node = jp.getCodec().readTree(jp);
		if (node.has("datafileExtension")) {
			meta.setDatafileExtension(node.get("datafileExtension").asText());
		} else {
			throw new IOException("Resource meta data must define the datafile extension");
		}
		if (node.has("keyType")) {
			try {
				meta.setKeyType(Class.forName(node.get("keyType").asText()));
			} catch (ClassNotFoundException err) {
				throw new IOException("Unable to load the entities key type", err);
			}
		} else {
			throw new IOException("Resource meta data must define the type of the entities key");
		}
		if (node.has("entityType")) {
			try {
				meta.setEntityType(Class.forName(node.get("entityType").asText()));
			} catch (ClassNotFoundException err) {
				throw new IOException("Unable to load the entities key type", err);
			}
		} else {
			throw new IOException("Resource meta data must define the type of the entities key");
		}
		
		return meta;
	}

}
