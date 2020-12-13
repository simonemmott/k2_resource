package org.k2.resource.entity;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.k2.resource.entity.MetaEntityResource;
import org.k2.resource.entity.model.EntityResourceManagerState;
import org.k2.resource.entity.simple.SimpleEntity;
import org.k2.resource.exception.DuplicateKeyError;
import org.k2.resource.exception.MissingKeyError;
import org.k2.resource.transaction.TransactionManager;

import static org.assertj.core.api.Assertions.*;

class EntityResourceManagerTest {
	
	@Test
	void testCreate() throws Exception {
		File dir = new File("testFilesystem/EntityResourceManagerTest/testCreate/newManagedResource");
		
		if (dir.exists()) FileUtils.forceDelete(dir);
		
		EntityResourceManager rm = EntityResourceManager.create(dir);
		
		assertThat(dir.exists()).isTrue();
		File transactionsDir = FileUtils.getFile(dir, TransactionManager.TRANSACTIONS_DIR_NAME);
		assertThat(transactionsDir.exists()).isTrue();
	}
	
	@Test
	void testOpen() throws Exception {
		File dir = new File("testFilesystem/EntityResourceManagerTest/testOpen/existingManagedResource");
		
		EntityResourceManager rm = EntityResourceManager.open(dir);
		
		assertThat(dir.exists()).isTrue();
		File transactionsDir = FileUtils.getFile(dir, TransactionManager.TRANSACTIONS_DIR_NAME);
		assertThat(transactionsDir.exists()).isTrue();
	}
	
	@Test
	void testGetState() throws Exception {
		File dir = new File("testFilesystem/EntityResourceManagerTest/testOpen/existingManagedResource");
		EntityResourceManager rm = EntityResourceManager.open(dir);
		
		EntityResourceManagerState result = rm.getState();
		
		assertThat(result.getResourcesDir()).endsWith("testFilesystem/EntityResourceManagerTest/testOpen/existingManagedResource");
	}
	
	
	
	
	
	
	
	
	
	
	
	

}
