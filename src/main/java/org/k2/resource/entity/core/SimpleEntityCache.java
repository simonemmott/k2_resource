package org.k2.resource.entity.core;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import org.k2.resource.binary.BinaryEntity;
import org.k2.resource.binary.BinaryResource;
import org.k2.resource.entity.exception.UnexpectedSerializationError;
import org.k2.resource.entity.serialize.EntitySerializationFactory;
import org.k2.resource.entity.serialize.EntitySerializer;
import org.k2.resource.exception.MissingKeyError;
import org.k2.resource.exception.MutatingEntityError;
import org.k2.resource.exception.UnexpectedResourceError;
import org.k2.util.binary.BinaryUtils;
import org.k2.util.object.ObjectUtils;

import lombok.Getter;

public class SimpleEntityCache implements EntityCache {
		
	public class CacheItem<E> {
		public E entity;
		public E clone = null;
		public boolean deleted = false;
		public String checksum = NEW_ENTITY;
	}
	
//	@Getter
//	private final ThreadLocal<MessageDigest> digest;
//	private final Map<Class<?>, EntitySerializer<?>> serializers;
	private final Map<Class<?>,Map<Object,CacheItem<?>>> cache;

	public SimpleEntityCache() {
//		this.digest = ThreadLocal.withInitial(() -> {
//			try {
//				return MessageDigest.getInstance("MD5");
//			} catch (NoSuchAlgorithmException e) {
//				throw new UnexpectedResourceError("Unable to create MD5 digest");
//			}
//		});
//		this.serializers = new HashMap<>();
		this.cache = new HashMap<>();
	}

//	public SimpleEntityCache(
//			ThreadLocal<MessageDigest> digest) {
//		this.digest = digest;
//		this.serializers = new HashMap<>();
//		this.cache = new HashMap<>();
//	}

//	public SimpleEntityCache(
//			ThreadLocal<MessageDigest> digest, 
//			Map<Class<?>, EntitySerializer<?>> serializers) {
//		this.digest = digest;
//		this.serializers = serializers;
//		this.cache = new HashMap<>();
//	}
	
//	public <E> SimpleEntityCache serialize(Class<E> type, EntitySerializer<E> serializer) {
//		serializers.put(type, serializer);
//		return this;
//		
//	}
	
//	public <E> EntitySerializer<E> getEntitySerializer(Class<E> type) {
//		EntitySerializer<E> serializer = (EntitySerializer<E>) serializers.get(type);
//		if (serializer == null) throw new UnexpectedSerializationError(type);
//		return serializer;
//	}
	
	private <E> Map<Object,CacheItem<?>> getCacheItems(Class<E> type) {
		if (!cache.containsKey(type)) {
			cache.put(type, new HashMap<>());
		}
		return cache.get(type);
	}

	private <E> CacheItem<E> getCacheItem(Class<E> type, Object key) {
		Map<Object,CacheItem<?>> cacheItems = getCacheItems(type);
		if (!cacheItems.containsKey(key)) {
			return null;
		}
		return (CacheItem<E>)cacheItems.get(key);
	}

	private <E> CacheItem<E> getOrCreateCacheItem(Class<E> type, Object key) {
		Map<Object,CacheItem<?>> cacheItems = getCacheItems(type);
		if (!cacheItems.containsKey(key)) {
			cacheItems.put(key, new CacheItem<E>());
		}
		return (CacheItem<E>)cacheItems.get(key);
	}
	
	private <E> MissingKeyError missingKeyError(Class<E> type, Object key) {
		return new MissingKeyError(
				MessageFormat.format("The cache does not contain an entity fo type: {0} with key: {1}",
						type.getName(), key.toString()));
	}
	
	@Override
	public <E> E get(Class<E> type, Object key) throws MissingKeyError {
		CacheItem<E> cacheItem = getCacheItem(type, key);
		if (cacheItem == null) throw missingKeyError(type, key);
		if (cacheItem.deleted) throw missingKeyError(type, key);
		return cacheItem.entity;
	}

	@Override
	public <E> void put(Class<E> type, Object key, E entity) throws MutatingEntityError {
		CacheItem<E> cacheItem = getOrCreateCacheItem(type, key);
		if (cacheItem.deleted) throw new MutatingEntityError("Unable to cache item. It has already been deleted");
		cacheItem.entity = entity;
	}

	@Override
	public <E> void put(Class<E> type, Object key, E entity, String checksum) {
		CacheItem<E> cacheItem = getOrCreateCacheItem(type, key);
		cacheItem.entity = entity;
		cacheItem.clone = ObjectUtils.deepclone(entity);
		cacheItem.checksum = checksum;
		cacheItem.deleted = false;
	}

	@Override
	public <E> E delete(Class<E> type, Object key) throws MissingKeyError, MutatingEntityError {
		CacheItem<E> cacheItem = getCacheItem(type, key);
		if (cacheItem == null) throw missingKeyError(type, key);
		if (cacheItem.deleted) throw new MutatingEntityError("Unable to delete item. It has already been deleted");
		cacheItem.deleted = true;
		return cacheItem.entity;
	}

	@Override
	public <E> boolean has(Class<E> type, Object key) {
		return getCacheItems(type).containsKey(key);
	}

	@Override
	public <E> boolean isNew(Class<E> type, Object key) throws MissingKeyError {
		CacheItem<E> cacheItem = getCacheItem(type, key);
		if (cacheItem == null) throw missingKeyError(type, key);
		return cacheItem.checksum.equals(NEW_ENTITY);
	}

	@Override
	public <E> boolean isDeleted(Class<E> type, Object key) throws MissingKeyError {
		CacheItem<E> cacheItem = getCacheItem(type, key);
		if (cacheItem == null) throw missingKeyError(type, key);
		return cacheItem.deleted;
	}

	@Override
	public <E> boolean isChanged(Class<E> type, Object key) throws MissingKeyError {
		CacheItem<E> cacheItem = getCacheItem(type, key);
		if (cacheItem == null) throw missingKeyError(type, key);
		if (cacheItem.clone == null) return true;
		if (cacheItem.deleted) return true;
		return !cacheItem.clone.equals(cacheItem.entity);
	}

	@Override
	public <E> String checksum(Class<E> type, Object key) throws MissingKeyError {
		CacheItem<E> cacheItem = getCacheItem(type, key);
		if (cacheItem == null) throw missingKeyError(type, key);
		return cacheItem.checksum;
	}
	
	@Override
	public <E> Set<E> fetch(Class<E> entityType) {
		Map<Object,CacheItem<?>> cachedItems = cache.get(entityType);
		if (cachedItems == null) return new HashSet();
		return cachedItems.values().stream()
				.filter((CacheItem<?> item) -> {return !item.deleted;})
				.map((CacheItem<?> item) -> {return (E)item.entity;})
				.collect(Collectors.toSet());		
	}

	@Override
	public <E> Set<Object> keys(Class<E> type) {
		Map<Object,CacheItem<?>> items = cache.get(type);
		if (items == null) return new HashSet<>();
		return items.entrySet().stream()
				.filter((Map.Entry<Object,CacheItem<?>> entry) -> {return !entry.getValue().deleted;})
				.map((Map.Entry<Object,CacheItem<?>> entry) -> {return entry.getKey();})
				.collect(Collectors.toSet());
	}

	@Override
	public <K,E> Set<K> keys(Class<E> type, Class<K> keyType) {
		Map<Object,CacheItem<?>> items = cache.get(type);
		if (items == null) return new HashSet<K>();
		return items.entrySet().stream()
				.filter((Map.Entry<Object,CacheItem<?>> entry) -> {return !entry.getValue().deleted;})
				.map((Map.Entry<Object,CacheItem<?>> entry) -> {return (K)entry.getKey();})
				.collect(Collectors.toSet());
	}

}
