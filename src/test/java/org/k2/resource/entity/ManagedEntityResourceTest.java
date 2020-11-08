package org.k2.resource.entity;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.k2.resource.entity.MetaEntityResource;
import org.k2.resource.entity.simple.SimpleEntity;
import org.k2.resource.entity.test.RefItem1;
import org.k2.resource.exception.DuplicateKeyError;
import org.k2.resource.exception.MissingKeyError;
import org.k2.resource.transaction.TransactionManager;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.fail;
import static org.apache.commons.io.filefilter.FileFilterUtils.*;

class ManagedEntityResourceTest {
	
	private final ObjectMapper mapper = new ObjectMapper();
	
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
	
	@Test
	void testCreateEntityCreatesTransaction() throws Exception {
		File dir = new File("testFilesystem/ManagedEntityResourceTest/testResources/r1");
		File transactionsDir = FileUtils.getFile(dir, TransactionManager.TRANSACTIONS_DIR_NAME);
		
		if (!dir.exists()) FileUtils.forceMkdir(dir);
		
		EntityResourceManager rm = EntityResourceManager.create(dir);
		
		ManagedEntityResource<String,RefItem1> refItem1Resource = new ManagedEntityResource<>(
				String.class,
				RefItem1.class,
				rm);
		
		RefItem1 item = new RefItem1();
		item.setKey("KEY");
		item.setName("NAME");
		item.setData(123);
		
		assertThatDirIsEmpty(transactionsDir);

		refItem1Resource.create("KEY", item);
		
		assertThatDirContainsItems(transactionsDir, 1);
		FileUtils.cleanDirectory(transactionsDir);
		
	}
	
	private void assertThatDirIsEmpty(File dir) {
		assertThatDirContainsItems(dir, 0);
	}
	
	private void assertThatDirContainsItems(File dir, int items) {
		try {
			assertThat(Files.list(dir.toPath()).count()).isEqualTo(items);
		} catch (IOException e) {
			fail();
			e.printStackTrace();
		}
	}
	
	
	@Test
	void testCreateEntityAndCommitSavesEntityAndRemovesTransaction() throws Exception {
		File dir = new File("testFilesystem/ManagedEntityResourceTest/testResources/r2");
		File transactionsDir = FileUtils.getFile(dir, TransactionManager.TRANSACTIONS_DIR_NAME);
		File refItem1Dir = FileUtils.getFile(dir, "RefItem1");
		File itemFile = FileUtils.getFile(refItem1Dir, "KEY.json");
		
		if (!dir.exists()) FileUtils.forceMkdir(dir);
		if (refItem1Dir.exists()) FileUtils.forceDelete(refItem1Dir);
		
		EntityResourceManager rm = EntityResourceManager.create(dir);
		
		ManagedEntityResource<String,RefItem1> refItem1Resource = new ManagedEntityResource<>(
				String.class,
				RefItem1.class,
				rm);
		
		RefItem1 item = new RefItem1();
		item.setKey("KEY");
		item.setName("NAME");
		item.setData(123);
		
		refItem1Resource.create("KEY", item);

		rm.getSession().getTransaction().commit();
		
		assertThatDirIsEmpty(transactionsDir);
		assertThat(itemFile).exists();
		
		RefItem1 committed = mapper.readValue(itemFile, RefItem1.class);
		assertThat(committed).isEqualTo(item);
		FileUtils.cleanDirectory(transactionsDir);
	}
	
	@Test
	void testCreateEntityAndRollbackDiscardsEntityAndRemovesTransaction() throws Exception {
		File dir = new File("testFilesystem/ManagedEntityResourceTest/testResources/r2");
		File transactionsDir = FileUtils.getFile(dir, TransactionManager.TRANSACTIONS_DIR_NAME);
		File refItem1Dir = FileUtils.getFile(dir, "RefItem1");
		File itemFile = FileUtils.getFile(refItem1Dir, "KEY.json");
		
		if (dir.exists()) FileUtils.forceDelete(dir);
		if (!dir.exists()) FileUtils.forceMkdir(dir);
		
		EntityResourceManager rm = EntityResourceManager.create(dir);
		
		ManagedEntityResource<String,RefItem1> refItem1Resource = new ManagedEntityResource<>(
				String.class,
				RefItem1.class,
				rm);
		
		RefItem1 item = new RefItem1();
		item.setKey("KEY");
		item.setName("NAME");
		item.setData(123);
		
		refItem1Resource.create("KEY", item);

		rm.getSession().getTransaction().rollback();
		
		assertThatDirIsEmpty(transactionsDir);
		assertThat(itemFile).doesNotExist();
		FileUtils.cleanDirectory(transactionsDir);
	}
	
	@Test
	void testGetReturnsCreatedEntity() throws Exception {
		File dir = new File("testFilesystem/ManagedEntityResourceTest/testResources/r2");
		File transactionsDir = FileUtils.getFile(dir, TransactionManager.TRANSACTIONS_DIR_NAME);
		File refItem1Dir = FileUtils.getFile(dir, "RefItem1");
		File itemFile = FileUtils.getFile(refItem1Dir, "KEY.json");
		
		if (!dir.exists()) FileUtils.forceMkdir(dir);
		if (refItem1Dir.exists()) FileUtils.forceDelete(refItem1Dir);
		
		EntityResourceManager rm = EntityResourceManager.create(dir);
		
		ManagedEntityResource<String,RefItem1> refItem1Resource = new ManagedEntityResource<>(
				String.class,
				RefItem1.class,
				rm);
		
		RefItem1 item = new RefItem1();
		item.setKey("KEY");
		item.setName("NAME");
		item.setData(123);
		
		refItem1Resource.create("KEY", item);
		
		RefItem1 getItem = refItem1Resource.get("KEY");
		assertThat(getItem).isEqualTo(item);

		rm.getSession().getTransaction().commit();
		
		RefItem1 committedItem = refItem1Resource.get("KEY");
		assertThat(committedItem).isEqualTo(item);
		FileUtils.cleanDirectory(transactionsDir);

	}
	
	@Test
	void testUpdateEntityCreatesTransaction() throws Exception {
		File dir = new File("testFilesystem/ManagedEntityResourceTest/testResources/r3");
		File transactionsDir = FileUtils.getFile(dir, TransactionManager.TRANSACTIONS_DIR_NAME);
		File refItem1Dir = FileUtils.getFile(dir, "RefItem1");
		File itemFile = FileUtils.getFile(refItem1Dir, "KEY.json");
		
		if (!dir.exists()) FileUtils.forceMkdir(dir);
		if (refItem1Dir.exists()) FileUtils.forceDelete(refItem1Dir);
		
		EntityResourceManager rm = EntityResourceManager.create(dir);
		
		ManagedEntityResource<String,RefItem1> refItem1Resource = new ManagedEntityResource<>(
				String.class,
				RefItem1.class,
				rm);
		
		RefItem1 item = new RefItem1();
		item.setKey("KEY");
		item.setName("NAME");
		item.setData(123);		
		refItem1Resource.create("KEY", item);		
		rm.getSession().getTransaction().commit();
		
		RefItem1 itemForUpdate = refItem1Resource.get("KEY");
		itemForUpdate.setName("UPDATED");
		itemForUpdate.setData(987);
		
		assertThatDirIsEmpty(transactionsDir);

		refItem1Resource.update("KEY", itemForUpdate);
		
		assertThatDirContainsItems(transactionsDir, 1);
		FileUtils.cleanDirectory(transactionsDir);
	}
	
	@Test
	void testUpdateEntityAndCommitSavesUpdateAndRemovesTransaction() throws Exception {
		File dir = new File("testFilesystem/ManagedEntityResourceTest/testResources/r3");
		File transactionsDir = FileUtils.getFile(dir, TransactionManager.TRANSACTIONS_DIR_NAME);
		File refItem1Dir = FileUtils.getFile(dir, "RefItem1");
		File itemFile = FileUtils.getFile(refItem1Dir, "KEY.json");
		
		if (!dir.exists()) FileUtils.forceMkdir(dir);
		if (refItem1Dir.exists()) FileUtils.forceDelete(refItem1Dir);
		
		EntityResourceManager rm = EntityResourceManager.create(dir);
		
		ManagedEntityResource<String,RefItem1> refItem1Resource = new ManagedEntityResource<>(
				String.class,
				RefItem1.class,
				rm);
		
		RefItem1 item = new RefItem1();
		item.setKey("KEY");
		item.setName("NAME");
		item.setData(123);		
		refItem1Resource.create("KEY", item);		
		rm.getSession().getTransaction().commit();
		
		RefItem1 itemForUpdate = refItem1Resource.get("KEY");
		itemForUpdate.setName("UPDATED");
		itemForUpdate.setData(987);
		
		refItem1Resource.update("KEY", itemForUpdate);
		
		assertThatDirContainsItems(transactionsDir, 1);
		
		rm.getSession().getTransaction().commit();
		
		assertThatDirIsEmpty(transactionsDir);

		RefItem1 committed = mapper.readValue(itemFile, RefItem1.class);
		assertThat(committed).isEqualTo(itemForUpdate);
		FileUtils.cleanDirectory(transactionsDir);


	}
	
	@Test
	void testUpdateEntityAndRollbackDiscardsUpdateAndRemovesTransaction() throws Exception {
		File dir = new File("testFilesystem/ManagedEntityResourceTest/testResources/r4");
		File transactionsDir = FileUtils.getFile(dir, TransactionManager.TRANSACTIONS_DIR_NAME);
		File refItem1Dir = FileUtils.getFile(dir, "RefItem1");
		File itemFile = FileUtils.getFile(refItem1Dir, "KEY.json");
		
		if (!dir.exists()) FileUtils.forceMkdir(dir);
		if (refItem1Dir.exists()) FileUtils.forceDelete(refItem1Dir);
		
		EntityResourceManager rm = EntityResourceManager.create(dir);
		
		ManagedEntityResource<String,RefItem1> refItem1Resource = new ManagedEntityResource<>(
				String.class,
				RefItem1.class,
				rm);
		
		RefItem1 item = new RefItem1();
		item.setKey("KEY");
		item.setName("NAME");
		item.setData(123);		
		refItem1Resource.create("KEY", item);		
		rm.getSession().getTransaction().commit();
		
		RefItem1 itemForUpdate = refItem1Resource.get("KEY");
		itemForUpdate.setName("UPDATED");
		itemForUpdate.setData(987);
		
		refItem1Resource.update("KEY", itemForUpdate);
		
		assertThatDirContainsItems(transactionsDir, 1);
		
		rm.getSession().getTransaction().rollback();
		
		assertThatDirIsEmpty(transactionsDir);

		RefItem1 committed = mapper.readValue(itemFile, RefItem1.class);
		assertThat(committed).isEqualTo(item);
		FileUtils.cleanDirectory(transactionsDir);


	}
	
	@Test
	void testSaveWithNewItemAndCommit() throws Exception {
		File dir = new File("testFilesystem/ManagedEntityResourceTest/testResources/r4");
		File transactionsDir = FileUtils.getFile(dir, TransactionManager.TRANSACTIONS_DIR_NAME);
		File refItem1Dir = FileUtils.getFile(dir, "RefItem1");
		File itemFile = FileUtils.getFile(refItem1Dir, "KEY.json");
		
		if (!dir.exists()) FileUtils.forceMkdir(dir);
		if (refItem1Dir.exists()) FileUtils.forceDelete(refItem1Dir);
		
		EntityResourceManager rm = EntityResourceManager.create(dir);
		
		ManagedEntityResource<String,RefItem1> refItem1Resource = new ManagedEntityResource<>(
				String.class,
				RefItem1.class,
				rm);
		
		RefItem1 item = new RefItem1();
		item.setKey("KEY");
		item.setName("NAME");
		item.setData(123);		
		refItem1Resource.save(item);		
		rm.getSession().getTransaction().commit();
		
		assertThat(refItem1Resource.exists("KEY")).isTrue();
		assertThat(itemFile).exists();
		RefItem1 committed = mapper.readValue(itemFile, RefItem1.class);
		assertThat(committed).isEqualTo(item);
		FileUtils.cleanDirectory(transactionsDir);

	}
	
	@Test
	void testSaveWithNewItemAndRollback() throws Exception {
		File dir = new File("testFilesystem/ManagedEntityResourceTest/testResources/r4");
		File transactionsDir = FileUtils.getFile(dir, TransactionManager.TRANSACTIONS_DIR_NAME);
		File refItem1Dir = FileUtils.getFile(dir, "RefItem1");
		File itemFile = FileUtils.getFile(refItem1Dir, "KEY.json");
		
		if (!dir.exists()) FileUtils.forceMkdir(dir);
		if (refItem1Dir.exists()) FileUtils.forceDelete(refItem1Dir);
		
		EntityResourceManager rm = EntityResourceManager.create(dir);
		
		ManagedEntityResource<String,RefItem1> refItem1Resource = new ManagedEntityResource<>(
				String.class,
				RefItem1.class,
				rm);
		
		RefItem1 item = new RefItem1();
		item.setKey("KEY");
		item.setName("NAME");
		item.setData(123);		
		refItem1Resource.save(item);		
		rm.getSession().getTransaction().rollback();
		
		assertThat(refItem1Resource.exists("KEY")).isFalse();
		assertThat(itemFile).doesNotExist();
		FileUtils.cleanDirectory(transactionsDir);
	}
	
	@Test
	void testSaveWithExistingItemAndCommit() throws Exception {
		File dir = new File("testFilesystem/ManagedEntityResourceTest/testResources/r4");
		File transactionsDir = FileUtils.getFile(dir, TransactionManager.TRANSACTIONS_DIR_NAME);
		File refItem1Dir = FileUtils.getFile(dir, "RefItem1");
		File itemFile = FileUtils.getFile(refItem1Dir, "KEY.json");
		
		if (!dir.exists()) FileUtils.forceMkdir(dir);
		if (refItem1Dir.exists()) FileUtils.forceDelete(refItem1Dir);
		
		EntityResourceManager rm = EntityResourceManager.create(dir);
		
		ManagedEntityResource<String,RefItem1> refItem1Resource = new ManagedEntityResource<>(
				String.class,
				RefItem1.class,
				rm);
		
		RefItem1 item = new RefItem1();
		item.setKey("KEY");
		item.setName("NAME");
		item.setData(123);		
		refItem1Resource.save(item);		
		rm.getSession().getTransaction().commit();
		
		RefItem1 itemForUpdate = refItem1Resource.get("KEY");
		itemForUpdate.setName("UPDATED");
		itemForUpdate.setData(987);
		
		refItem1Resource.save(itemForUpdate);		
		rm.getSession().getTransaction().commit();

		assertThat(refItem1Resource.exists("KEY")).isTrue();
		assertThat(itemFile).exists();

		RefItem1 committed = mapper.readValue(itemFile, RefItem1.class);
		assertThat(committed).isEqualTo(itemForUpdate);
		FileUtils.cleanDirectory(transactionsDir);
	}
	
	@Test
	void testSaveWithExistingItemAndRollback() throws Exception {
		File dir = new File("testFilesystem/ManagedEntityResourceTest/testResources/r4");
		File transactionsDir = FileUtils.getFile(dir, TransactionManager.TRANSACTIONS_DIR_NAME);
		File refItem1Dir = FileUtils.getFile(dir, "RefItem1");
		File itemFile = FileUtils.getFile(refItem1Dir, "KEY.json");
		
		if (!dir.exists()) FileUtils.forceMkdir(dir);
		if (refItem1Dir.exists()) FileUtils.forceDelete(refItem1Dir);
		
		EntityResourceManager rm = EntityResourceManager.create(dir);
		
		ManagedEntityResource<String,RefItem1> refItem1Resource = new ManagedEntityResource<>(
				String.class,
				RefItem1.class,
				rm);
		
		RefItem1 item = new RefItem1();
		item.setKey("KEY");
		item.setName("NAME");
		item.setData(123);		
		refItem1Resource.save(item);		
		rm.getSession().getTransaction().commit();
		
		RefItem1 itemForUpdate = refItem1Resource.get("KEY");
		itemForUpdate.setName("UPDATED");
		itemForUpdate.setData(987);
		
		refItem1Resource.save(itemForUpdate);		
		rm.getSession().getTransaction().rollback();

		assertThat(refItem1Resource.exists("KEY")).isTrue();
		assertThat(itemFile).exists();

		RefItem1 committed = mapper.readValue(itemFile, RefItem1.class);
		assertThat(committed).isEqualTo(item);
		FileUtils.cleanDirectory(transactionsDir);
	}
	
	@Test
	void testSessionSaveWithExistingItemAndCommit() throws Exception {
		File dir = new File("testFilesystem/ManagedEntityResourceTest/testResources/r4");
		File transactionsDir = FileUtils.getFile(dir, TransactionManager.TRANSACTIONS_DIR_NAME);
		File refItem1Dir = FileUtils.getFile(dir, "RefItem1");
		File itemFile = FileUtils.getFile(refItem1Dir, "KEY.json");
		
		if (!dir.exists()) FileUtils.forceMkdir(dir);
		if (refItem1Dir.exists()) FileUtils.forceDelete(refItem1Dir);
		
		EntityResourceManager rm = EntityResourceManager.create(dir);
		
		ManagedEntityResource<String,RefItem1> refItem1Resource = new ManagedEntityResource<>(
				String.class,
				RefItem1.class,
				rm);
		
		RefItem1 item = new RefItem1();
		item.setKey("KEY");
		item.setName("NAME");
		item.setData(123);		
		refItem1Resource.save(item);		
		rm.getSession().getTransaction().commit();
		
		RefItem1 itemForUpdate = refItem1Resource.get("KEY");
		itemForUpdate.setName("UPDATED");
		itemForUpdate.setData(987);
		
		rm.getSession().save();		
		rm.getSession().getTransaction().commit();

		assertThat(refItem1Resource.exists("KEY")).isTrue();
		assertThat(itemFile).exists();

		RefItem1 committed = mapper.readValue(itemFile, RefItem1.class);
		assertThat(committed).isEqualTo(itemForUpdate);
		FileUtils.cleanDirectory(transactionsDir);
	}
	
	@Test
	void testDoInTransactionWithExistingEntity() throws Throwable{
		File dir = new File("testFilesystem/ManagedEntityResourceTest/testResources/r4");
		File transactionsDir = FileUtils.getFile(dir, TransactionManager.TRANSACTIONS_DIR_NAME);
		File refItem1Dir = FileUtils.getFile(dir, "RefItem1");
		File itemFile = FileUtils.getFile(refItem1Dir, "KEY.json");
		
		if (!dir.exists()) FileUtils.forceMkdir(dir);
		if (refItem1Dir.exists()) FileUtils.forceDelete(refItem1Dir);
		
		EntityResourceManager rm = EntityResourceManager.create(dir);
		
		ManagedEntityResource<String,RefItem1> refItem1Resource = new ManagedEntityResource<>(
				String.class,
				RefItem1.class,
				rm);
		
		RefItem1 item = new RefItem1();
		item.setKey("KEY");
		item.setName("NAME");
		item.setData(123);		
		refItem1Resource.save(item);		
		rm.getSession().getTransaction().commit();
		RefItem1 saved = mapper.readValue(itemFile, RefItem1.class);
		assertThat(saved).isEqualTo(item);

		RefItem1[] items = new RefItem1[1];
		
		rm.getSession().doInTransaction(() -> {
			items[0] = refItem1Resource.get("KEY");
			items[0].setName("UPDATED");
			items[0].setData(987);			
		});
		
		assertThat(refItem1Resource.exists("KEY")).isTrue();
		assertThat(itemFile).exists();

		RefItem1 committed = mapper.readValue(itemFile, RefItem1.class);
		assertThat(committed).isEqualTo(items[0]);
		assertThat(committed).isNotEqualTo(item);
		FileUtils.cleanDirectory(transactionsDir);
	}
	
	
	
	
	
	
	
	
	
	
	

}
