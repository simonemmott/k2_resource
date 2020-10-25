package org.k2.resource.entity.serialize;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;
import org.k2.resource.entity.annotation.Key;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

public class DefaultEntitySerializationTest {
	
	@AllArgsConstructor
	@NoArgsConstructor
	@EqualsAndHashCode
	public static class TypeWithKeyMember {
		@Getter(onMethod_= @__({@Key}))
		@Setter(onMethod_= @__({@Key}))
		private String someKey;
		@Getter
		private String notKey;
	}

	@Test
	public void testSerialize() throws Exception {
		DefaultEntitySerializer<TypeWithKeyMember> serializer = new DefaultEntitySerializer<>(TypeWithKeyMember.class);
		TypeWithKeyMember entity = new TypeWithKeyMember("KEY", "NOT_KEY");
		assertThat(serializer.serialize(entity)).isNotNull();
	}

	@Test
	public void testDeserialize() throws Exception {
		DefaultEntitySerializer<TypeWithKeyMember> serializer = new DefaultEntitySerializer<>(TypeWithKeyMember.class);
		DefaultEntityDeserializer<TypeWithKeyMember> deserializer = new DefaultEntityDeserializer<>(TypeWithKeyMember.class);
		TypeWithKeyMember entity = new TypeWithKeyMember("KEY", "NOT_KEY");
		
		assertThat(deserializer.deserialize(serializer.serialize(entity))).isEqualTo(entity);
	}

}
