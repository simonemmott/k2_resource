package org.k2.resource.binary;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import org.apache.commons.io.FileUtils;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.k2.resource.binary.BinaryEntityImpl;
import org.k2.resource.exception.DuplicateKeyError;
import org.k2.resource.exception.MissingKeyError;
import org.k2.resource.exception.UnexpectedResourceError;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class BinaryResourceTest {
	
	public BinaryResourceTest() {}
	
	private static final ThreadLocal<MessageDigest> checksum = ThreadLocal.withInitial(
			() -> {
				try {
					return MessageDigest.getInstance("MD5");
				} catch (NoSuchAlgorithmException e) {
					throw new UnexpectedResourceError("Unable to create MD5 digest");
				}
			});
	
	@Test
	public void testNewBinaryResourceLoadsDataFiles() throws Exception {		
		File resourceDir = new File("testFilesystem/BinaryResourceTest/BS1");
		BinaryResource resource = new BinaryResource(resourceDir, checksum);		
		assertThat(resource.count()).isEqualTo(2);
	}
	
	@Test
	public void testGet() throws Exception {		
		File resourceDir = new File("testFilesystem/BinaryResourceTest/BS1");
		BinaryResource resource = new BinaryResource(resourceDir, checksum);	
		
		BinaryEntity fred = resource.get("FredFlintstone");
		assertThat(fred).isNotNull();
		File fredFile = new File("testFilesystem/BinaryResourceTest/BS1/FredFlintstone.json");
		assertThat(fred.getKey()).isEqualTo("FredFlintstone");
		assertThat(fred.getData()).isEqualTo(FileUtils.readFileToByteArray(fredFile));
		assertThat(fred.getChecksum()).isEqualTo("a04b3a0ce6bb09772efb18abe0c41371");
		
		BinaryEntity barney = resource.get("BarneyRubble");
		assertThat(barney).isNotNull();
		File barneyFile = new File("testFilesystem/BinaryResourceTest/BS1/BarneyRubble.json");
		assertThat(barney.getKey()).isEqualTo("BarneyRubble");
		assertThat(barney.getData()).isEqualTo(FileUtils.readFileToByteArray(barneyFile));
		assertThat(barney.getChecksum()).isEqualTo("d70a99c9ba4449306a0803f8c297df7c");
	}

	@Test
	public void testGetThrowsMissingKEyError() throws Exception {		
		File resourceDir = new File("testFilesystem/BinaryResourceTest/BS1");
		BinaryResource resource = new BinaryResource(resourceDir, checksum);
		
		assertThatThrownBy(() -> {
			resource.get("XXXX");
		}).isInstanceOf(MissingKeyError.class);
	}
	
	@Test
	public void testExists() throws Exception {
		File resourceDir = new File("testFilesystem/BinaryResourceTest/BS1");
		BinaryResource resource = new BinaryResource(resourceDir, checksum);
		assertThat(resource.exists("FredFlintstone")).isTrue();
		assertThat(resource.exists("BarneyRubble")).isTrue();
		assertThat(resource.exists("XXXX")).isFalse();
		
	}

	@Test
	public void testFetch() throws Exception {
		File resourceDir = new File("testFilesystem/BinaryResourceTest/BS1");
		BinaryResource resource = new BinaryResource(resourceDir, checksum);
		assertThat(resource.fetch()).contains(resource.get("FredFlintstone"));
		assertThat(resource.fetch()).contains(resource.get("BarneyRubble"));
		assertThat(resource.fetch().size()).isEqualTo(resource.count());		
	}
	
	@Test
	@Ignore
	public void testCreate() throws Exception {
		File resourceDir = new File("testFilesystem/BinaryResourceTest/testCreate");
		FileUtils.cleanDirectory(resourceDir);
		BinaryResource resource = new BinaryResource(resourceDir, checksum);
		
		File dataFile = new File("testFilesystem/BinaryResourceTest/testCreate/XXXX."
				+resource.getDatafileExtension());
		BinaryEntity be = new BinaryEntityImpl("XXXX", "AAAA".getBytes());
		assertThat(be.getChecksum()).isEqualTo("NEW_ENTITY");
		
		BinaryEntity savedBe = resource.create("XXXX", be);
		
		assertThat(savedBe).isEqualTo(be);
		assertThat(savedBe.getChecksum()).isNotEqualTo("NEW_ENTITY");
		assertThat(dataFile).exists();
		assertThat(resource.keys()).contains("XXXX");
		dataFile.delete();
	}
	
	@Test
	public void testCreateThrowsDuplicateKeyException() throws Exception {
		File resourceDir = new File("testFilesystem/BinaryResourceTest/BS1");
		BinaryResource resource = new BinaryResource(resourceDir, checksum);
		BinaryEntityImpl be = new BinaryEntityImpl("AAAA", "AAAA".getBytes());
		
		assertThatThrownBy(() -> {
			resource.create("FredFlintstone", be);
		}).isInstanceOf(DuplicateKeyError.class);
	}
	
	@Test
	@Ignore
	public void testUpdate() throws Exception {
		File resourceDir = new File("testFilesystem/BinaryResourceTest/testUpdate");
		FileUtils.cleanDirectory(resourceDir);
		BinaryResource resource = new BinaryResource(resourceDir, checksum);
		
		File dataFile = new File("testFilesystem/BinaryResourceTest/testUpdate/XXXX."
				+resource.getDatafileExtension());
		BinaryEntity be = new BinaryEntityImpl("XXXX", "AAAA".getBytes());
		be = resource.create("XXXX", be);
		
		be.setData("BBBB".getBytes());
		BinaryEntity beUpdated = resource.update("XXXX", be);
		
		assertThat(FileUtils.readFileToByteArray(dataFile)).isEqualTo("BBBB".getBytes());
		assertThat(beUpdated.getChecksum()).isNotEqualTo(be.getChecksum());
		assertThat(beUpdated.getData()).isEqualTo("BBBB".getBytes());
		
		dataFile.delete();
	}
	
	@Test
	public void testSaveCallsCreateWhenObjIsNew() throws Exception {
		File resourceDir = new File("testFilesystem/BinaryResourceTest/testSave");
		FileUtils.cleanDirectory(resourceDir);
		BinaryResource resource = new BinaryResource(resourceDir, checksum);
		
		BinaryResource resourceSpy = spy(resource);
		
		BinaryEntityImpl mockEntity = mock(BinaryEntityImpl.class);
		doReturn(mockEntity).when(resourceSpy).create(any(), any());
		
		BinaryEntityImpl be = new BinaryEntityImpl("AAAA", "AAAA".getBytes());
		BinaryEntity savedBe = resourceSpy.save(be);
		
		assertThat(savedBe).isEqualTo(mockEntity);
		verify(resourceSpy, times(1)).create(eq("AAAA"), eq(be));
	}
	
	@Test
	public void testSaveCallsUpdateWhenObjIsNotNew() throws Exception {
		File resourceDir = new File("testFilesystem/BinaryResourceTest/testSave");
		FileUtils.cleanDirectory(resourceDir);
		BinaryResource resource = new BinaryResource(resourceDir, checksum);
		BinaryEntity be = resource.create("AAAA", new BinaryEntityImpl("AAAA", "AAAA".getBytes()));
		
		BinaryResource resourceSpy = spy(resource);
		
		BinaryEntityImpl mockEntity = mock(BinaryEntityImpl.class);
		doReturn(mockEntity).when(resourceSpy).update(any(), any());
		
		be.setData("XXXX".getBytes());
		
		BinaryEntity updatedBe = resourceSpy.save(be);
		
		assertThat(updatedBe).isEqualTo(mockEntity);
		verify(resourceSpy, times(1)).update(eq("AAAA"), eq(be));
	}
	
	@Test
	@Ignore
	public void testRemove() throws Exception {
		File resourceDir = new File("testFilesystem/BinaryResourceTest/testDelete");
		FileUtils.cleanDirectory(resourceDir);
		BinaryResource resource = new BinaryResource(resourceDir, checksum);
		BinaryEntity be = resource.create("AAAA", new BinaryEntityImpl("AAAA", "AAAA".getBytes()));
		File dataFile = new File("testFilesystem/BinaryResourceTest/testDelete/AAAA."
				+resource.getDatafileExtension());
		assertThat(dataFile).exists();
		assertThat(resource.keys()).contains("AAAA");
		
		BinaryEntity deletedBe = resource.remove("AAAA");
		
		assertThat(deletedBe.getKey()).isEqualTo("AAAA");
		assertThat(deletedBe.getData()).isEqualTo("AAAA".getBytes());
		assertThat(deletedBe.isDeleted()).isTrue();
		assertThat(dataFile).doesNotExist();
		assertThat(resource.keys()).doesNotContain("AAAA");
		
	}
	
	@Test
	@Ignore
	public void testDelete() throws Exception {
		File resourceDir = new File("testFilesystem/BinaryResourceTest/testDelete");
		FileUtils.cleanDirectory(resourceDir);
		BinaryResource resource = new BinaryResource(resourceDir, checksum);
		BinaryEntity be = resource.create("AAAA", new BinaryEntityImpl("AAAA", "AAAA".getBytes()));
		
		BinaryResource resourceSpy = spy(resource);
		
		doReturn(mock(BinaryEntityImpl.class)).when(resourceSpy).remove(anyString());
		
		resourceSpy.delete(be);
		
		verify(resourceSpy, times(1)).remove(eq("AAAA"));
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	

}
