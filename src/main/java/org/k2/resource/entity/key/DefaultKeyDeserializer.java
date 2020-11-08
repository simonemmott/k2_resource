package org.k2.resource.entity.key;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.zip.GZIPInputStream;

import org.k2.resource.binary.KeyDeserializer;
import org.k2.resource.binary.KeySerializer;
import org.k2.resource.entity.exception.UnexpectedKeyError;

import com.fasterxml.jackson.databind.ObjectMapper;

public class DefaultKeyDeserializer<K> implements KeyDeserializer<K> {

	private final Class<K> keyType;
	private final KeyDeserializer<K> keyDeserializer;
	
	public DefaultKeyDeserializer (Class<K> keyType) {
		this.keyType = keyType;
		
		if (keyType == Integer.class) {
			this.keyDeserializer = (KeyDeserializer<K>) getDefaultIntegerDeserializer();
			return;
		}
		if (keyType == Long.class) {
			this.keyDeserializer = (KeyDeserializer<K>) getDefaultLongDeserializer();
			return;
		}
		if (keyType == Float.class) {
			this.keyDeserializer = (KeyDeserializer<K>) getDefaultFloatDeserializer();
			return;
		}
		if (keyType == Double.class) {
			this.keyDeserializer = (KeyDeserializer<K>) getDefaultDoubleDeserializer();
			return;
		}
		if (keyType == String.class) {
			this.keyDeserializer = (KeyDeserializer<K>) getDefaultStringDeserializer();
			return;
		}
		if (keyType.isAssignableFrom(Date.class)) {
			this.keyDeserializer = (KeyDeserializer<K>) getDefaultDateDeserializer();
			return;
		}
		this.keyDeserializer = getDefaultObjectDeserializer(keyType);
	}
	
	public static KeyDeserializer<Integer> getDefaultIntegerDeserializer() {
		return (String keyStr) -> {
			return Integer.valueOf(keyStr);
		};
	}

	public static KeyDeserializer<Long> getDefaultLongDeserializer() {
		return (String keyStr) -> {
			return Long.valueOf(keyStr);
		};
	}

	public static KeyDeserializer<Float> getDefaultFloatDeserializer() {
		return (String keyStr) -> {
			return Float.valueOf(keyStr);
		};
	}

	public static KeyDeserializer<Double> getDefaultDoubleDeserializer() {
		return (String keyStr) -> {
			return Double.valueOf(keyStr);
		};
	}

	public static KeyDeserializer<Boolean> getDefaultBooleanDeserializer() {		
		return (String keyStr) -> {
			return (keyStr.equalsIgnoreCase("TRUE"));
		};
	}

	public static KeyDeserializer<String> getDefaultStringDeserializer() {		
		return (String keyStr) -> {
			try {
				return URLDecoder.decode(keyStr, StandardCharsets.UTF_8.toString());
			} catch (UnsupportedEncodingException e) {
				throw new UnexpectedKeyError(e);
			}
		};
	}

	public static KeyDeserializer<Date> getDefaultDateDeserializer() {		
		return (String keyStr) -> {
			return new Date(Long.valueOf(keyStr));
		};
	}
	
	public static <K> KeyDeserializer<K> getDefaultObjectDeserializer(Class<K> keyClass) {
		ObjectMapper mapper = new ObjectMapper();
		return (String keyStr) -> {
			try {
				byte[] decoded = Base64.getDecoder().decode(keyStr.getBytes());
				ByteArrayInputStream bais = new ByteArrayInputStream(decoded); 
				GZIPInputStream gzis = new GZIPInputStream(bais);
				BufferedReader br = new BufferedReader(new InputStreamReader(gzis, "UTF-8"));
				StringBuilder sb = new StringBuilder();
				String line;
				while((line = br.readLine()) != null) {
					sb.append(line);
				}
				br.close();
				gzis.close();
				bais.close();
				String keyJson = sb.toString();
				return mapper.readValue(keyJson, keyClass);
			} catch (IOException e) {
				throw new UnexpectedKeyError(e);
			}
		};
	}

	@Override
	public K deserialize(String keyStr) {
		return keyDeserializer.deserialize(keyStr);
	}

}
