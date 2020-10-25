package org.k2.resource.entity.key;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Base64;
import java.util.Date;
import java.util.Locale;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.k2.resource.binary.KeyDeserializer;
import org.k2.resource.binary.KeySerializer;
import org.k2.resource.entity.exception.UnexpectedKeyError;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;

@Getter
public class DefaultKeySerializer<K> implements KeySerializer<K> {
	
	private static DecimalFormat df = getDecimalFormat();
	
	private static DecimalFormat getDecimalFormat() {
		DecimalFormat df = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
		df.setMaximumFractionDigits(340);
		return df;
	}

	private final Class<K> keyType;
	private final KeySerializer<K> keySerializer;

	
	public DefaultKeySerializer (Class<K> keyType) {
		this.keyType = keyType;
		
		if (keyType == String.class) {
			this.keySerializer = (KeySerializer<K>) getDefaultStringSerializer();
			return;
		}
		if (keyType == Double.class) {
			this.keySerializer = (KeySerializer<K>) getDefaultDoubleSerializer();
			return;
		}
		if (Number.class.isAssignableFrom(keyType)) {
			this.keySerializer = (KeySerializer<K>) getDefaultNumberSerializer();
			return;
		}
		if (Date.class.isAssignableFrom(keyType)) {
			this.keySerializer = (KeySerializer<K>) getDefaultDateSerializer();
			return;
		}
		if (keyType == Boolean.class) {
			this.keySerializer = (KeySerializer<K>) getDefaultBooleanSerializer();
			return;
		}
		this.keySerializer = (KeySerializer<K>) getDefaultObjectSerializer();
	}
	
	public static KeySerializer<Number> getDefaultNumberSerializer() {
		return (Number key) -> {
			return key.toString();
		};
	}

	public static KeySerializer<Double> getDefaultDoubleSerializer() {		
		return (Double key) -> {
			return df.format(key);
		};
	}

	public static KeySerializer<Boolean> getDefaultBooleanSerializer() {		
		return (Boolean key) -> {
			return (key) ? "TRUE" : "FALSE";
		};
	}

	public static KeySerializer<String> getDefaultStringSerializer() {		
		return (String key) -> {
			try {
				return URLEncoder.encode(key, StandardCharsets.UTF_8.toString());
			} catch (UnsupportedEncodingException e) {
				throw new UnexpectedKeyError(e);
			}
		};
	}

	public static KeySerializer<Date> getDefaultDateSerializer() {		
		return (Date key) -> {
			return Long.valueOf(key.getTime()).toString();
		};
	}
	
	public static KeySerializer<Object> getDefaultObjectSerializer() {
		ObjectMapper mapper = new ObjectMapper();
		return (Object key) -> {
			try {
				String keyJson = mapper.writeValueAsString(key);
				ByteArrayOutputStream out = new ByteArrayOutputStream();
		        GZIPOutputStream gzip = new GZIPOutputStream(out);
		        gzip.write(keyJson.getBytes());
		        gzip.close();
		        return new String(Base64.getEncoder().encode(out.toByteArray()));
			} catch (IOException e) {
				throw new UnexpectedKeyError(e);
			}
		};
	}

	@Override
	public String serialize(K key) {
		return keySerializer.serialize(key);
	}

}
