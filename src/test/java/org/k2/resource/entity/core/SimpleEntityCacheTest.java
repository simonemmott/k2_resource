package org.k2.resource.entity.core;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.powermock.reflect.Whitebox;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.security.MessageDigest;
import java.util.zip.Checksum;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.k2.resource.entity.MetaEntityResource;
import org.k2.resource.entity.annotation.Key;
import org.k2.resource.entity.annotation.RefItem;
import org.k2.resource.entity.exception.EntityConfigurationException;
import org.k2.resource.entity.serialize.DefaultEntitySerializationFactory;
import org.k2.resource.entity.serialize.EntitySerializationFactory;
import org.k2.resource.entity.serialize.EntitySerializer;
import org.k2.resource.exception.MissingKeyError;
import org.k2.resource.exception.ResourceConfigurationException;
import org.k2.util.binary.BinaryUtils;
import org.k2.util.object.ObjectUtils;
import org.mockito.BDDMockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;

@RunWith(PowerMockRunner.class)
class SimpleEntityCacheTest {
	
	private SimpleEntityCache cache;
	
	static SimpleEntityCache testCache() {
		SimpleEntityCache cache = new SimpleEntityCache();
		return cache;
	}
	
	private static <E> EntitySerializer<E> getEntitySerializer(Class<E> type) throws EntityConfigurationException {
		MetaEntityResource meta = new MetaEntityResource();
		meta.setKeyType(String.class);
		meta.setEntityType(type);
		meta.setDatafileExtension("json");
		
		return new DefaultEntitySerializationFactory<String,E>(String.class, type, meta)
				.create(String.class, type).getSerializer();
	}

	@Test
	void testContextLoads() throws Exception {
		cache = testCache();
		assertThat(cache).isNotNull();
	}
	
	@Test
	void testPutGet() throws Exception {
		cache = testCache();
		TestEntity1 e1_1 = new TestEntity1("1");
		TestEntity1 e1_2 = new TestEntity1("2");
		TestEntity2 e2_1 = new TestEntity2("1");
		TestEntity2 e2_2 = new TestEntity2("2");
		
		cache.put(TestEntity1.class, "1", e1_1);
		cache.put(TestEntity1.class, "2", e1_2);
		cache.put(TestEntity2.class, "1", e2_1);
		cache.put(TestEntity2.class, "2", e2_2);
		
		assertThat(cache.get(TestEntity1.class, "1")).isEqualTo(e1_1);
		assertThat(cache.get(TestEntity1.class, "2")).isEqualTo(e1_2);
		assertThat(cache.get(TestEntity2.class, "1")).isEqualTo(e2_1);
		assertThat(cache.get(TestEntity2.class, "2")).isEqualTo(e2_2);
	}
	
	@Test
	void testGetThrowsMissingKeyError() {
		cache = testCache();
		assertThatThrownBy(() -> cache.get(TestEntity1.class, "1"))
				.isInstanceOf(MissingKeyError.class);
	}
	
	@Test
	void testDeleteThrowsMissingKeyError() {
		cache = testCache();
		assertThatThrownBy(() -> cache.delete(TestEntity1.class, "1"))
				.isInstanceOf(MissingKeyError.class);
	}
	
	@Test
	void testDelete() throws Exception {
		cache = testCache();
		TestEntity1 e1_1 = new TestEntity1("1");
		cache.put(TestEntity1.class, "1", e1_1);
		assertThat(cache.isDeleted(TestEntity1.class, "1")).isFalse();
		assertThat(cache.delete(TestEntity1.class, "1"))
				.isEqualTo(e1_1);
		assertThat(cache.isDeleted(TestEntity1.class, "1")).isTrue();
		assertThat(cache.has(TestEntity1.class, "1")).isTrue();
	}
	
	@Test
	void testHas() throws Exception {
		cache = testCache();
		TestEntity1 e1_1 = new TestEntity1("1");
		assertThat(cache.has(TestEntity1.class, "1")).isFalse();
		cache.put(TestEntity1.class, "1", e1_1);
		assertThat(cache.has(TestEntity1.class, "1")).isTrue();
	}
	
	@Test
	void testIsNewThrowsMissingKeyError() {
		cache = testCache();
		assertThatThrownBy(() -> cache.isNew(TestEntity1.class, "1"))
				.isInstanceOf(MissingKeyError.class);
	}
	
	@Test
	void testIsDeletedThrowsMissingKeyError() {
		cache = testCache();
		assertThatThrownBy(() -> cache.isDeleted(TestEntity1.class, "1"))
				.isInstanceOf(MissingKeyError.class);
	}
	
	@Test
	void testIsNew() throws Exception {
		cache = testCache();
		TestEntity1 e1_1 = new TestEntity1("1");
		cache.put(TestEntity1.class, "1", e1_1);
		assertThat(cache.isNew(TestEntity1.class, "1"))
				.isTrue();
	}

	@Test
	void testIsChangedThrowsMissingKeyError() {
		cache = testCache();
		assertThatThrownBy(() -> cache.isChanged(TestEntity1.class, "1"))
				.isInstanceOf(MissingKeyError.class);
	}
	
	@Test
	void testIsChanged() throws Exception {
		cache = testCache();
		TestEntity1 e1_1 = new TestEntity1("1");
		cache.put(TestEntity1.class, "1", e1_1);
		assertThat(cache.isChanged(TestEntity1.class, "1")).isTrue();
		
		TestEntity1 e1_2 = new TestEntity1("2");
		cache.put(TestEntity1.class, "2", e1_2, "checksum");
		
		assertThat(cache.isChanged(TestEntity1.class, "2")).isFalse();
		e1_2.key="AAA";
		assertThat(cache.isChanged(TestEntity1.class, "2")).isTrue();
	}
	


}








