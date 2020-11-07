package org.k2.resource.entity;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.k2.resource.entity.MetaEntityResource;
import org.k2.resource.entity.simple.SimpleEntity;
import org.k2.resource.entity.test.RefItem1;
import org.k2.resource.exception.DuplicateKeyError;
import org.k2.resource.exception.MissingKeyError;
import org.k2.resource.transaction.TransactionManager;

import static org.assertj.core.api.Assertions.*;

class ManagedEntityResourceTest {
	
	@Test
	void testNewWithoutResource() throws Exception {
		File dir = new File("testFilesystem/ManagedEntityResourceTest/testNewWithoutResource/resource");
		File refItem1dir = new File("testFilesystem/ManagedEntityResourceTest/testNewWithoutResource/resource/RefItem1");
		
		if (!dir.exists()) FileUtils.forceMkdir(dir);
		if (!refItem1dir.exists()) FileUtils.forceDelete(dir);
		
		EntityResourceManager rm = EntityResourceManager.create(dir);
		
		ManagedEntityResource<String,RefItem1> refItem1Resource = new ManagedEntityResource<>(
				String.class,
				RefItem1.class,
				rm);
		
		assertThat(refItem1dir.exists()).isTrue();
		assertThat(rm.getResource(RefItem1.class)).isEqualTo(refItem1Resource);
	}
	
	@Test
	void testNewWithResource() throws Exception {
		File dir = new File("testFilesystem/ManagedEntityResourceTest/testNewWithResource/resource");
		File refItem1dir = new File("testFilesystem/ManagedEntityResourceTest/testNewWithResource/resource/RefItem1");
		
		if (!dir.exists()) FileUtils.forceMkdir(dir);
		if (!refItem1dir.exists()) FileUtils.forceMkdir(refItem1dir);
		
		EntityResourceManager rm = EntityResourceManager.create(dir);
		
		ManagedEntityResource<String,RefItem1> refItem1Resource = new ManagedEntityResource<>(
				String.class,
				RefItem1.class,
				rm);
		
		assertThat(refItem1dir.exists()).isTrue();
		assertThat(rm.getResource(RefItem1.class)).isEqualTo(refItem1Resource);
	}
	
	
	
	
	
	
	
	
	
	
	

}
