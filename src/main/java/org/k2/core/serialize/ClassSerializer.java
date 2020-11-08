package org.k2.core.serialize;

import java.io.IOException;

import org.k2.resource.MetaResource;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class ClassSerializer extends StdSerializer<Class<?>> {

	public ClassSerializer() {
		this(null);
	}

	public ClassSerializer(Class<Class<?>> type) {
		super(type);
	}

	@Override
	public void serialize(Class<?> cls, JsonGenerator gen, SerializerProvider provider) throws IOException {
		gen.writeString(cls.getCanonicalName());
	}

}
