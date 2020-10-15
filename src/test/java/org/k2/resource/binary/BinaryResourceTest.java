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
import org.k2.resource.binary.BinaryEntityFactory;
import org.k2.resource.exception.MissingKeyError;

class BinaryResourceTest {
	
	private static final Checksum checksum = new CRC32();
	
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
		try {
			resource.get("XXXX");
			fail("MissingKeyError not thrown");
		} catch (MissingKeyError err) {
			
		}
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

}
