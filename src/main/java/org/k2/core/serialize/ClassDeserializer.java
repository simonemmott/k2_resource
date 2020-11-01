package org.k2.core.serialize;

import java.io.IOException;

import org.k2.resource.MetaResource;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class ClassDeserializer extends StdDeserializer<Class<?>> {

	public ClassDeserializer() {
		super(Class.class);
	}

	@Override
	public Class<?> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		JsonNode node = jp.getCodec().readTree(jp);
		if (node.isTextual()) {
			try {
				return  Class.forName(node.asText());
			} catch (ClassNotFoundException err) {
				throw new IOException("Unable to identify class for: "+node.asText(), err);
			}
		}
		throw new IOException("Only text nodes can be deserialized into a Class");
	}

}
