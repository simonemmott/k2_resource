package org.k2.resource.entity.key;

import static org.assertj.core.api.Assertions.*;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.Base64;
import java.util.Date;
import java.util.zip.GZIPInputStream;

import org.junit.Test;
import org.k2.resource.entity.annotation.Key;
import org.k2.resource.entity.exception.UnexpectedKeyError;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class DefaultKeyDeserializerTest {
	
	@Test
	public void testDefaultIntegerDeserializer() throws Exception {
		assertThat(DefaultKeyDeserializer
				.getDefaultIntegerDeserializer()
				.deserialize("123")).isEqualTo(123);
	}

	@Test
	public void testDefaultLongDeserializer() throws Exception {
		assertThat(DefaultKeyDeserializer
				.getDefaultLongDeserializer()
				.deserialize("1234567890123456789")).isEqualTo(1234567890123456789L);
	}

	@Test
	public void testDefaultFloatDeserializer() throws Exception {
		assertThat(DefaultKeyDeserializer
				.getDefaultFloatDeserializer()
				.deserialize("123.456")).isEqualTo(123.456f);
	}

	@Test
	public void testDefaultDoubleDeserializer() throws Exception {
		assertThat(DefaultKeyDeserializer
				.getDefaultDoubleDeserializer()
				.deserialize("123456789.12345678")).isEqualTo(123456789.12345678d);
	}

	@Test
	public void testDefaultStringDeserializer() throws Exception {
		assertThat(DefaultKeyDeserializer
				.getDefaultStringDeserializer()
				.deserialize("%21%40%C2%A3%24%25%5E%26*%28%29+%60%7E")).isEqualTo("!@£$%^&*() `~");
		assertThat(DefaultKeyDeserializer
				.getDefaultStringDeserializer()
				.deserialize("hellow+world%21")).isEqualTo("hellow world!");
	}

	@Test
	public void testDefaultDateDeserializer() throws Exception {
		assertThat(DefaultKeyDeserializer
				.getDefaultDateDeserializer()
				.deserialize("123456789012345")).isEqualTo(new Date(123456789012345L));
	}

	@Test
	public void testDefaultObjectDeserializer() throws Exception {
		assertThat(DefaultKeyDeserializer
				.getDefaultObjectDeserializer(SomeKey.class)
				.deserialize("H4sIAAAAAAAAAKtWKs7PTQ0uKcrMS1eyUnJ0cnZR0gGLeeaVpKanFilZGRoZm0CEXBJLUiF801oAJ5GFSzkAAAA="))
		.isEqualTo(new SomeKey("ABCD", 1234, new Date(12345)));
	}

}
