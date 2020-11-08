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

public class DefaultKeySerializerTest {
	
	@Test
	public void testDefaultNumberSerializer() throws Exception {
		assertThat(DefaultKeySerializer
				.getDefaultNumberSerializer()
				.serialize(123)).isEqualTo("123");
		assertThat(DefaultKeySerializer
				.getDefaultNumberSerializer()
				.serialize(1234567890123456789L)).isEqualTo("1234567890123456789");
		assertThat(DefaultKeySerializer
				.getDefaultNumberSerializer()
				.serialize(10000000000000000L)).isEqualTo("10000000000000000");
		assertThat(new DefaultKeySerializer<Integer>(Integer.class).serialize(123456789))
		.isEqualTo("123456789");
		assertThat(new DefaultKeySerializer<Long>(Long.class).serialize(1234567890123456789L))
		.isEqualTo("1234567890123456789");
		assertThat(new DefaultKeySerializer<Long>(Long.class).serialize(10000000000000000L))
		.isEqualTo("10000000000000000");
	}

	@Test
	public void testDefaultDoubleSerializer() throws Exception {
		assertThat(DefaultKeySerializer
				.getDefaultDoubleSerializer()
				.serialize(123456789.12345678d)).isEqualTo("123456789.12345678");
		assertThat(new DefaultKeySerializer<Double>(Double.class).serialize(123456789.12345678d))
			.isEqualTo("123456789.12345678");
	}

	@Test
	public void testDefaultStringSerializer() throws Exception {
		assertThat(DefaultKeySerializer
				.getDefaultStringSerializer()
				.serialize("!@£$%^&*() `~")).isEqualTo("%21%40%C2%A3%24%25%5E%26*%28%29+%60%7E");
		assertThat(DefaultKeySerializer
				.getDefaultStringSerializer()
				.serialize("hellow world!")).isEqualTo("hellow+world%21");
		assertThat(new DefaultKeySerializer<String>(String.class).serialize("!@£$%^&*() `~"))
		.isEqualTo("%21%40%C2%A3%24%25%5E%26*%28%29+%60%7E");
		assertThat(new DefaultKeySerializer<String>(String.class).serialize("hellow world!"))
		.isEqualTo("hellow+world%21");
	}

	@Test
	public void testDefaultDateSerializer() throws Exception {
		assertThat(DefaultKeySerializer
				.getDefaultDateSerializer()
				.serialize(new Date(123456789012345L))).isEqualTo("123456789012345");
		assertThat(new DefaultKeySerializer<Date>(Date.class).serialize(new Date(123456789012345L)))
		.isEqualTo("123456789012345");
	}

	@Test
	public void testDefaultObjectSerializer() throws Exception {
		assertThat(DefaultKeySerializer
				.getDefaultObjectSerializer()
				.serialize(new SomeKey("ABCD", 1234, new Date(12345))))
		.isEqualTo("H4sIAAAAAAAAAKtWKs7PTQ0uKcrMS1eyUnJ0cnZR0gGLeeaVpKanFilZGRoZm0CEXBJLUiF801oAJ5GFSzkAAAA=");
		assertThat(new DefaultKeySerializer<SomeKey>(SomeKey.class).serialize(new SomeKey("ABCD", 1234, new Date(12345))))
		.isEqualTo("H4sIAAAAAAAAAKtWKs7PTQ0uKcrMS1eyUnJ0cnZR0gGLeeaVpKanFilZGRoZm0CEXBJLUiF801oAJ5GFSzkAAAA=");
	}

}
