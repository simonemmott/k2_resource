package org.k2.resource.location;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
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
import org.k2.util.binary.BinaryUtils;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class DigestableResourceTest {
		
	@Test
	void testGetKey() throws Exception {
		File rFile = new File("testFilesystem/DigestableResourceTest/key_1.json");
		DigestableResource r = DigestableResource.create(rFile);
		assertThat(r).isNotNull();
		assertThat(r.getKey()).isEqualTo("key_1");
	}
	
	@Test
	void testGetData() throws Exception {
		File rFile = new File("testFilesystem/DigestableResourceTest/key_1.json");
		DigestableResource r = DigestableResource.create(rFile);

		assertThat(r.getData()).isEqualTo(FileUtils.readFileToByteArray(rFile));
	}
	
	@Test
	void testSetData() throws Exception {
		File rFile = new File("testFilesystem/DigestableResourceTest/key_2.json");
		DigestableResource r = DigestableResource.create(rFile);
		byte[] data = new byte[10];
	    new Random().nextBytes(data);

		assertThat(r.getData()).isEqualTo(FileUtils.readFileToByteArray(rFile));
		r.setData(data);
		assertThat(r.getData()).isEqualTo(data);
		assertThat(r.getData()).isEqualTo(FileUtils.readFileToByteArray(rFile));

		byte[] expectedDigest = DigestableLocation.defaultDigestor().digest(FileUtils.readFileToByteArray(rFile));
		assertThat(r.getDigest()).isEqualTo(expectedDigest);
		String expectedChecksum = BinaryUtils.hex(expectedDigest);
		assertThat(r.getChecksum()).isEqualTo(expectedChecksum);

	}
	
	@Test
	void testGetDigest() throws Exception {
		File rFile = new File("testFilesystem/DigestableResourceTest/key_1.json");
		DigestableResource r = DigestableResource.create(rFile);
		byte[] expectedDigest = DigestableLocation.defaultDigestor().digest(FileUtils.readFileToByteArray(rFile));

		assertThat(r.getDigest()).isEqualTo(expectedDigest);
	}
	
	
	@Test
	void testGetChecksum() throws Exception {
		File rFile = new File("testFilesystem/DigestableResourceTest/key_1.json");
		DigestableResource r = DigestableResource.create(rFile);
		byte[] expectedDigest = DigestableLocation.defaultDigestor().digest(FileUtils.readFileToByteArray(rFile));
		String expectedChecksum = BinaryUtils.hex(expectedDigest);
		assertThat(r.getChecksum()).isEqualTo(expectedChecksum);
	}
	
	@Test
	void testGetDatafile() throws Exception {
		File rFile = new File("testFilesystem/DigestableResourceTest/key_1.json");
		DigestableResource r = DigestableResource.create(rFile);
		assertThat(r.getDatafile()).isEqualTo(rFile);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
