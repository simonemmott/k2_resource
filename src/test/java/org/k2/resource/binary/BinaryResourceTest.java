package org.k2.resource.binary;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;
import org.k2.resource.binary.BinaryEntity;
import org.k2.resource.exception.DuplicateKeyError;
import org.k2.resource.exception.MissingKeyError;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class BinaryResourceTest {
	
	private static final ThreadLocal<Checksum> checksum = ThreadLocal.withInitial(
			() -> {
				return new CRC32();
			});
	
	@Test
	void testNewBinaryResourceLoadsDataFiles() throws Exception {		
		File resourceDir = new File("testFilesystem/BinaryResourceTest/BS1");
		BinaryResource resource = new BinaryResource(resourceDir, checksum);		
		assertThat(resource.count()).isEqualTo(2);
	}
	
	@Test
	void testGet() throws Exception {		
		File resourceDir = new File("testFilesystem/BinaryResourceTest/BS1");
		BinaryResource resource = new BinaryResource(resourceDir, checksum);	
		
		BinaryEntity fred = resource.get("FredFlintstone");
		assertThat(fred).isNotNull();
		File fredFile = new File("testFilesystem/BinaryResourceTest/BS1/FredFlintstone.json");
		assertThat(fred.getKey()).isEqualTo("FredFlintstone");
		assertThat(fred.getData()).isEqualTo(FileUtils.readFileToByteArray(fredFile));
		assertThat(fred.getChecksum()).isEqualTo(1301605181L);
		
		BinaryEntity barney = resource.get("BarneyRubble");
		assertThat(barney).isNotNull();
		File barneyFile = new File("testFilesystem/BinaryResourceTest/BS1/BarneyRubble.json");
		assertThat(barney.getKey()).isEqualTo("BarneyRubble");
		assertThat(barney.getData()).isEqualTo(FileUtils.readFileToByteArray(barneyFile));
		assertThat(barney.getChecksum()).isEqualTo(4212237153L);
	}

	@Test
	void testGetThrowsMissingKEyError() throws Exception {		
		File resourceDir = new File("testFilesystem/BinaryResourceTest/BS1");
		BinaryResource resource = new BinaryResource(resourceDir, checksum);
		
		assertThatThrownBy(() -> {
			resource.get("XXXX");
		}).isInstanceOf(MissingKeyError.class);
	}
	
	@Test
	void testExists() throws Exception {
		File resourceDir = new File("testFilesystem/BinaryResourceTest/BS1");
		BinaryResource resource = new BinaryResource(resourceDir, checksum);
		assertThat(resource.exists("FredFlintstone")).isTrue();
		assertThat(resource.exists("BarneyRubble")).isTrue();
		assertThat(resource.exists("XXXX")).isFalse();
		
	}

	@Test
	void testFetch() throws Exception {
		File resourceDir = new File("testFilesystem/BinaryResourceTest/BS1");
		BinaryResource resource = new BinaryResource(resourceDir, checksum);
		assertThat(resource.fetch()).contains(resource.get("FredFlintstone"));
		assertThat(resource.fetch()).contains(resource.get("BarneyRubble"));
		assertThat(resource.fetch().size()).isEqualTo(resource.count());		
	}
	
	@Test
	void testCreate() throws Exception {
		File resourceDir = new File("testFilesystem/BinaryResourceTest/testCreate");
		FileUtils.cleanDirectory(resourceDir);
		BinaryResource resource = new BinaryResource(resourceDir, checksum);
		
		File dataFile = new File("testFilesystem/BinaryResourceTest/testCreate/XXXX."
				+resource.getDatafileExtension());
		BinaryEntity be = new BinaryEntity("XXXX", "AAAA".getBytes());
		assertThat(be.getChecksum()).isEqualTo(-1);
		
		BinaryWrapper savedBe = resource.create("XXXX", be);
		
		assertThat(savedBe).isEqualTo(be);
		assertThat(savedBe.getChecksum()).isNotEqualTo(-1);
		assertThat(dataFile).exists();
		assertThat(resource.keys()).contains("XXXX");
		dataFile.delete();
	}
	
	@Test
	void testCreateThrowsDuplicateKeyException() throws Exception {
		File resourceDir = new File("testFilesystem/BinaryResourceTest/BS1");
		BinaryResource resource = new BinaryResource(resourceDir, checksum);
		BinaryEntity be = new BinaryEntity("AAAA", "AAAA".getBytes());
		
		assertThatThrownBy(() -> {
			resource.create("FredFlintstone", be);
		}).isInstanceOf(DuplicateKeyError.class);
	}
	
	@Test
	void testUpdate() throws Exception {
		File resourceDir = new File("testFilesystem/BinaryResourceTest/testUpdate");
		FileUtils.cleanDirectory(resourceDir);
		BinaryResource resource = new BinaryResource(resourceDir, checksum);
		
		File dataFile = new File("testFilesystem/BinaryResourceTest/testUpdate/XXXX."
				+resource.getDatafileExtension());
		BinaryWrapper be = new BinaryEntity("XXXX", "AAAA".getBytes());
		be = resource.create("XXXX", be);
		
		be.setData("BBBB".getBytes());
		BinaryWrapper beUpdated = resource.update("XXXX", be);
		
		assertThat(FileUtils.readFileToByteArray(dataFile)).isEqualTo("BBBB".getBytes());
		assertThat(beUpdated.getChecksum()).isNotEqualTo(be.getChecksum());
		assertThat(beUpdated.getData()).isEqualTo("BBBB".getBytes());
		
		dataFile.delete();
	}
	
	@Test
	void testSaveCallsCreateWhenObjIsNew() throws Exception {
		File resourceDir = new File("testFilesystem/BinaryResourceTest/testSave");
		FileUtils.cleanDirectory(resourceDir);
		BinaryResource resource = new BinaryResource(resourceDir, checksum);
		
		BinaryResource resourceSpy = spy(resource);
		
		BinaryEntity mockEntity = mock(BinaryEntity.class);
		doReturn(mockEntity).when(resourceSpy).create(any(), any());
		
		BinaryEntity be = new BinaryEntity("AAAA", "AAAA".getBytes());
		BinaryWrapper savedBe = resourceSpy.save(be);
		
		assertThat(savedBe).isEqualTo(mockEntity);
		verify(resourceSpy, times(1)).create(eq("AAAA"), eq(be));
	}
	
	@Test
	void testSaveCallsUpdateWhenObjIsNotNew() throws Exception {
		File resourceDir = new File("testFilesystem/BinaryResourceTest/testSave");
		FileUtils.cleanDirectory(resourceDir);
		BinaryResource resource = new BinaryResource(resourceDir, checksum);
		BinaryWrapper be = resource.create("AAAA", new BinaryEntity("AAAA", "AAAA".getBytes()));
		
		BinaryResource resourceSpy = spy(resource);
		
		BinaryEntity mockEntity = mock(BinaryEntity.class);
		doReturn(mockEntity).when(resourceSpy).update(any(), any());
		
		be.setData("XXXX".getBytes());
		
		BinaryWrapper updatedBe = resourceSpy.save(be);
		
		assertThat(updatedBe).isEqualTo(mockEntity);
		verify(resourceSpy, times(1)).update(eq("AAAA"), eq(be));
	}
	
	@Test
	void testRemove() throws Exception {
		File resourceDir = new File("testFilesystem/BinaryResourceTest/testDelete");
		FileUtils.cleanDirectory(resourceDir);
		BinaryResource resource = new BinaryResource(resourceDir, checksum);
		BinaryWrapper be = resource.create("AAAA", new BinaryEntity("AAAA", "AAAA".getBytes()));
		File dataFile = new File("testFilesystem/BinaryResourceTest/testDelete/AAAA."
				+resource.getDatafileExtension());
		assertThat(dataFile).exists();
		assertThat(resource.keys()).contains("AAAA");
		
		BinaryWrapper deletedBe = resource.remove("AAAA");
		
		assertThat(deletedBe.getKey()).isEqualTo("AAAA");
		assertThat(deletedBe.getData()).isEqualTo("AAAA".getBytes());
		assertThat(deletedBe.isDeleted()).isTrue();
		assertThat(dataFile).doesNotExist();
		assertThat(resource.keys()).doesNotContain("AAAA");
		
	}
	
	@Test
	void testDelete() throws Exception {
		File resourceDir = new File("testFilesystem/BinaryResourceTest/testDelete");
		FileUtils.cleanDirectory(resourceDir);
		BinaryResource resource = new BinaryResource(resourceDir, checksum);
		BinaryWrapper be = resource.create("AAAA", new BinaryEntity("AAAA", "AAAA".getBytes()));
		
		BinaryResource resourceSpy = spy(resource);
		
		doReturn(mock(BinaryEntity.class)).when(resourceSpy).remove(anyString());
		
		resourceSpy.delete(be);
		
		verify(resourceSpy, times(1)).remove(eq("AAAA"));
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	

}
