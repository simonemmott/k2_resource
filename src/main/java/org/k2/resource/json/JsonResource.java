package org.k2.resource.json;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.Checksum;

import org.k2.resource.Resource;
import org.k2.resource.binary.BinaryEntity;
import org.k2.resource.binary.BinaryResource;
import org.k2.resource.binary.exception.BinaryResourceInitializeException;
import org.k2.resource.exception.DuplicateKeyError;
import org.k2.resource.exception.MissingKeyError;
import org.k2.resource.exception.MutatingEntityError;
import org.k2.resource.exception.UnexpectedResourceError;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class JsonResource<K> implements Resource<K, JsonNode>{
	
	public interface KeyGetter<K> {
		K getKey(JsonNode jsonNode);
	}
	public interface KeySetter<K> {
		void setKey(JsonNode jsonNode, K key);
	}
	public interface KeySerializer<K> {
		String serialize(K key);
	}
	public interface KeyDeserializer<K> {
		K deserialize(String key);
	}
	
	public enum SerialStyle {
		COMPACT_JSON,
		PRETTY_JSON,
		YAML
	}

	private final BinaryResource resource;
	private final KeyGetter<K> keyGetter;
	private final KeySetter<K> keySetter;
	private final KeySerializer<K> keySerializer;
	private final KeyDeserializer<K> keyDeserializer;
	private final ObjectMapper mapper;
	private final ObjectWriter writer;
	
	public JsonResource(
			File dir,
			ThreadLocal<Checksum> checksum,
			KeyGetter<K> keyGetter,
			KeySetter<K> keySetter,
			KeySerializer<K> keySerializer,
			KeyDeserializer<K> keyDeserializer,
			SerialStyle style
			) throws BinaryResourceInitializeException {
		this.resource = new BinaryResource(dir, checksum);
		this.keyGetter = keyGetter;
		this.keySetter = keySetter;
		this.keySerializer = keySerializer;
		this.keyDeserializer = keyDeserializer;
		if (style == SerialStyle.YAML) {
			this.mapper = new ObjectMapper(new YAMLFactory());
		} else {
			this.mapper = new ObjectMapper();
		}
		if (style == SerialStyle.PRETTY_JSON) {
			this.writer = this.mapper.writerWithDefaultPrettyPrinter();
		} else {
			this.writer = this.mapper.writer();
		}		
	}
	
	private BinaryEntity entity(K key, JsonNode obj) {
		try {
			return new BinaryEntity(keySerializer.serialize(key), writer.writeValueAsBytes(obj));
		} catch (JsonProcessingException err) {
			throw new UnexpectedResourceError(err);
		}
	}

	@Override
	public JsonNode create(K key, JsonNode obj) throws DuplicateKeyError, MutatingEntityError {
		try {
			BinaryEntity entity = entity(key, obj);
			entity = resource.create(entity.getKey(), entity);
			return mapper.readTree(entity.getData());
		} catch (IOException err) {
			throw new UnexpectedResourceError(err);
		}
	}

	@Override
	public JsonNode get(K key) throws MissingKeyError {
		BinaryEntity entity = resource.get(keySerializer.serialize(key));
		try {
			return mapper.readTree(entity.getData());
		} catch (IOException err) {
			throw new UnexpectedResourceError(err);
		}
	}

	@Override
	public JsonNode update(K key, JsonNode obj) throws MissingKeyError, MutatingEntityError {
		try {
			BinaryEntity entity = entity(key, obj);
			entity = resource.update(entity.getKey(), entity);
			return mapper.readTree(entity.getData());
		} catch (IOException err) {
			throw new UnexpectedResourceError(err);
		}
	}

	@Override
	public JsonNode save(JsonNode obj) throws MissingKeyError, MutatingEntityError, DuplicateKeyError {
		try {
			BinaryEntity entity = entity(keyGetter.getKey(obj), obj);
			entity = resource.save(entity);
			return mapper.readTree(entity.getData());
		} catch (IOException err) {
			throw new UnexpectedResourceError(err);
		}
	}

	@Override
	public List<JsonNode> fetch() {
		try {
			List<BinaryEntity> entities = resource.fetch();
			List<JsonNode> jsonEntities = new ArrayList<>(entities.size());
			for (BinaryEntity entity : entities) {
				jsonEntities.add(mapper.readTree(entity.getData()));
			}
			return jsonEntities;
		} catch (IOException err) {
			throw new UnexpectedResourceError(err);
		}
	}

	@Override
	public JsonNode remove(K key) throws MissingKeyError, MutatingEntityError {
		try {
			BinaryEntity entity = resource.remove(keySerializer.serialize(key));
			return mapper.readTree(entity.getData());
		} catch (IOException err) {
			throw new UnexpectedResourceError(err);
		}
	}

	@Override
	public void delete(JsonNode obj) throws MissingKeyError, MutatingEntityError {
		resource.delete(entity(keyGetter.getKey(obj), obj));
	}

	@Override
	public int count() {
		return resource.count();
	}

	@Override
	public boolean exists(K key) {
		return resource.exists(keySerializer.serialize(key));
	}

	@Override
	public Set<K> keys() {
		Set<String> keyStrs = resource.keys();
		Set<K> keys = new HashSet<>(keyStrs.size());
		for (String keyStr : keyStrs) {
			keys.add(keyDeserializer.deserialize(keyStr));
		}
		return keys;
	}


}
