package org.k2.resource.entity.simple;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;
import org.k2.resource.binary.BinaryEntityImpl;
import org.k2.resource.exception.DuplicateKeyError;
import org.k2.resource.exception.MissingKeyError;
import org.k2.resource.exception.UnexpectedResourceError;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class SimpleEntityResourceTest {
	
	private static final ThreadLocal<MessageDigest> checksum = ThreadLocal.withInitial(
			() -> {
				try {
					return MessageDigest.getInstance("MD5");
				} catch (NoSuchAlgorithmException e) {
					throw new UnexpectedResourceError("Unable to create MD5 digest");
				}
			});
	
	@Test
	void testNewBinaryResourceLoadsDataFiles() throws Exception {		
		File resourceDir = new File("testFilesystem/EntityResourceTest/SimpleEntityResourceTest/resource1");
		SimpleEntityResource<String,SimpleEntity> resource = new SimpleEntityResource<>(
				String.class, 
				SimpleEntity.class, 
				resourceDir, 
				checksum);		
		assertThat(resource.count()).isEqualTo(2);
	}
	
	@Test
	void testGet() throws Exception {		
		File resourceDir = new File("testFilesystem/EntityResourceTest/SimpleEntityResourceTest/resource1");
		SimpleEntityResource<String,SimpleEntity> resource = new SimpleEntityResource<>(
				String.class, 
				SimpleEntity.class, 
				resourceDir, 
				checksum);		
		
		SimpleEntity fred = resource.get("FredFlintstone");
		assertThat(fred).isNotNull();
		assertThat(fred.getKey()).isEqualTo("FredFlintstone");
		assertThat(fred.getData()).isEqualTo(123);
		assertThat(fred.getName()).isEqualTo("Fred Flintstone");
		
		SimpleEntity barney = resource.get("BarneyRubble");
		assertThat(barney).isNotNull();
		assertThat(barney.getKey()).isEqualTo("BarneyRubble");
		assertThat(barney.getData()).isEqualTo(456);
		assertThat(barney.getName()).isEqualTo("Barney Rubble");
	}

	@Test
	void testGetThrowsMissingKeyError() throws Exception {		
		File resourceDir = new File("testFilesystem/EntityResourceTest/SimpleEntityResourceTest/resource1");
		SimpleEntityResource<String,SimpleEntity> resource = new SimpleEntityResource<>(
				String.class, 
				SimpleEntity.class, 
				resourceDir, 
				checksum);		
		
		assertThatThrownBy(() -> {
			resource.get("XXXX");
		}).isInstanceOf(MissingKeyError.class);
	}
	
	@Test
	void testExists() throws Exception {
		File resourceDir = new File("testFilesystem/EntityResourceTest/SimpleEntityResourceTest/resource1");
		SimpleEntityResource<String,SimpleEntity> resource = new SimpleEntityResource<>(
				String.class, 
				SimpleEntity.class, 
				resourceDir, 
				checksum);		
		assertThat(resource.exists("FredFlintstone")).isTrue();
		assertThat(resource.exists("BarneyRubble")).isTrue();
		assertThat(resource.exists("XXXX")).isFalse();
		
	}

	@Test
	void testFetch() throws Exception {
		File resourceDir = new File("testFilesystem/EntityResourceTest/SimpleEntityResourceTest/resource1");
		SimpleEntityResource<String,SimpleEntity> resource = new SimpleEntityResource<>(
				String.class, 
				SimpleEntity.class, 
				resourceDir, 
				checksum);		
		assertThat(resource.fetch()).contains(resource.get("FredFlintstone"));
		assertThat(resource.fetch()).contains(resource.get("BarneyRubble"));
		assertThat(resource.fetch().size()).isEqualTo(resource.count());		
	}
	
	@Test
	void testCreate() throws Exception {
		File resourceDir = new File("testFilesystem/EntityResourceTest/SimpleEntityResourceTest/resource2");
		FileUtils.listFiles(resourceDir, null, false).stream()
		.forEach(file -> {
			if (!file.getName().equals("__meta__.yml")) {
				file.delete();
			}
		});
		File dataFile = new File("testFilesystem/EntityResourceTest/SimpleEntityResourceTest/resource2/XXXX.json");
		if (dataFile.exists()) dataFile.delete();
		SimpleEntityResource<String,SimpleEntity> resource = new SimpleEntityResource<>(
				String.class, 
				SimpleEntity.class, 
				resourceDir, 
				checksum);		
		
		SimpleEntity entity = new SimpleEntity("XXXX", "XXXX Name", 789);
		
		SimpleEntity savedEntity = resource.create("XXXX", entity);
		
		assertThat(savedEntity).isEqualTo(entity);
		assertThat(dataFile).exists();
		assertThat(resource.keys()).contains("XXXX");
		dataFile.delete();
	}
	
	@Test
	void testCreateThrowsDuplicateKeyException() throws Exception {
		File resourceDir = new File("testFilesystem/EntityResourceTest/SimpleEntityResourceTest/resource1");
		SimpleEntityResource<String,SimpleEntity> resource = new SimpleEntityResource<>(
				String.class, 
				SimpleEntity.class, 
				resourceDir, 
				checksum);		
		SimpleEntity entity = new SimpleEntity("AAAA", "AAAA Name", 234);
		
		assertThatThrownBy(() -> {
			resource.create("FredFlintstone", entity);
		}).isInstanceOf(DuplicateKeyError.class);
	}
	
	@Test
	void testUpdate() throws Exception {
		File resourceDir = new File("testFilesystem/EntityResourceTest/SimpleEntityResourceTest/resource3");
		FileUtils.listFiles(resourceDir, null, false).stream()
		.forEach(file -> {
			if (!file.getName().equals("__meta__.yml")) {
				file.delete();
			}
		});
		SimpleEntityResource<String,SimpleEntity> resource = new SimpleEntityResource<>(
				String.class, 
				SimpleEntity.class, 
				resourceDir, 
				checksum);		
		
		File dataFile = new File("testFilesystem/EntityResourceTest/SimpleEntityResourceTest/resource3/XXXX.json");
		SimpleEntity entity = new SimpleEntity("XXXX", "XXXX Name", 345);
		entity = resource.create("XXXX", entity);
		byte[] initBytes = FileUtils.readFileToByteArray(dataFile);
		
		entity.setData(999);
		SimpleEntity updatedEntity = resource.update("XXXX", entity);
		byte[] updatedBytes = FileUtils.readFileToByteArray(dataFile);
		
		assertThat(updatedBytes).isNotEqualTo(initBytes);		
		dataFile.delete();
	}
	
	@Test
	void testSaveCallsCreateWhenObjIsNew() throws Exception {
		File resourceDir = new File("testFilesystem/EntityResourceTest/SimpleEntityResourceTest/resource4");
		FileUtils.listFiles(resourceDir, null, false).stream()
		.forEach(file -> {
			if (!file.getName().equals("__meta__.yml")) {
				file.delete();
			}
		});
		SimpleEntityResource<String,SimpleEntity> resource = new SimpleEntityResource<>(
				String.class, 
				SimpleEntity.class, 
				resourceDir, 
				checksum);		
		
		SimpleEntityResource<String,SimpleEntity> resourceSpy = spy(resource);
		
		SimpleEntity mockEntity = mock(SimpleEntity.class);
		doReturn(mockEntity).when(resourceSpy).create(any(), any());
		
		SimpleEntity entity = new SimpleEntity("AAAA", "AAAA Name", 123);
		SimpleEntity savedEntity = resourceSpy.save(entity);
		
		assertThat(savedEntity).isEqualTo(mockEntity);
		verify(resourceSpy, times(1)).create(eq("AAAA"), eq(entity));
	}
	
	@Test
	void testSaveCallsUpdateWhenObjIsNotNew() throws Exception {
		File resourceDir = new File("testFilesystem/EntityResourceTest/SimpleEntityResourceTest/resource4");
		FileUtils.listFiles(resourceDir, null, false).stream()
				.forEach(file -> {
					if (!file.getName().equals("__meta__.yml")) {
						file.delete();
					}
				});
		SimpleEntityResource<String,SimpleEntity> resource = new SimpleEntityResource<>(
				String.class, 
				SimpleEntity.class, 
				resourceDir, 
				checksum);		
		SimpleEntity entity = resource.create("AAAA", new SimpleEntity("AAAA", "AAAA Name", 123));
		
		SimpleEntityResource<String,SimpleEntity> resourceSpy = spy(resource);
		
		SimpleEntity mockEntity = mock(SimpleEntity.class);
		doReturn(mockEntity).when(resourceSpy).update(any(), any());
		
		entity.setData(9999);
		
		SimpleEntity updatedEntity = resourceSpy.save(entity);
		
		assertThat(updatedEntity).isEqualTo(mockEntity);
		verify(resourceSpy, times(1)).update(eq("AAAA"), eq(entity));
	}
	
	@Test
	void testRemove() throws Exception {
		File resourceDir = new File("testFilesystem/EntityResourceTest/SimpleEntityResourceTest/resource5");
		FileUtils.listFiles(resourceDir, null, false).stream()
		.forEach(file -> {
			if (!file.getName().equals("__meta__.yml")) {
				file.delete();
			}
		});
		SimpleEntityResource<String,SimpleEntity> resource = new SimpleEntityResource<>(
				String.class, 
				SimpleEntity.class, 
				resourceDir, 
				checksum);		
		SimpleEntity entity =  resource.create("AAAA", new SimpleEntity("AAAA", "AAAA Name", 123));
		File dataFile = new File("testFilesystem/EntityResourceTest/SimpleEntityResourceTest/resource5/AAAA.json");
		assertThat(dataFile).exists();
		assertThat(resource.keys()).contains("AAAA");
		
		SimpleEntity deletedEntity = resource.remove("AAAA");
		
		assertThat(deletedEntity.getKey()).isEqualTo("AAAA");
		assertThat(deletedEntity.getData()).isEqualTo(123);
		assertThat(dataFile).doesNotExist();
		assertThat(resource.keys()).doesNotContain("AAAA");
		
	}
	
	@Test
	void testDelete() throws Exception {
		File resourceDir = new File("testFilesystem/EntityResourceTest/SimpleEntityResourceTest/resource5");
		FileUtils.listFiles(resourceDir, null, false).stream()
		.forEach(file -> {
			if (!file.getName().equals("__meta__.yml")) {
				file.delete();
			}
		});
		SimpleEntityResource<String,SimpleEntity> resource = new SimpleEntityResource<>(
				String.class, 
				SimpleEntity.class, 
				resourceDir, 
				checksum);		
		SimpleEntity entity = resource.create("AAAA", new SimpleEntity("AAAA", "AAAA Name", 123));
		
		SimpleEntityResource<String,SimpleEntity> resourceSpy = spy(resource);
		
		doReturn(mock(BinaryEntityImpl.class)).when(resourceSpy).remove(anyString());
		
		resourceSpy.delete(entity);
		
		verify(resourceSpy, times(1)).remove(eq("AAAA"));
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	

}
