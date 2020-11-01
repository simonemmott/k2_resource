package org.k2.resource.location;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.k2.resource.entity.MetaEntityResource;
import org.k2.resource.entity.simple.SimpleEntity;
import org.k2.resource.exception.DuplicateKeyError;
import org.k2.resource.exception.MissingKeyError;

import static org.assertj.core.api.Assertions.*;

class DigestableLocationTest {
	
	@Test
	void testContextLoads() throws Exception {
		File lFile = new File("testFilesystem/DigestableLocationTest/test1");
		DigestableLocation l = DigestableLocation.create(lFile);
		assertThat(l).isNotNull();
		assertThat(l.getName()).isEqualTo("test1");
	}
	
	@Test
	void testHasMetadata() throws Exception {
		File lFile = new File("testFilesystem/DigestableLocationTest/test1");
		DigestableLocation l = DigestableLocation.create(lFile);
		
		assertThat(l.hasMetadata()).isFalse();
		assertThat(l.getLocation("location1").hasMetadata()).isTrue();
		assertThat(l.getLocation("location2").hasMetadata()).isTrue();
	}
	
	@Test
	void testGetMetadata() throws Exception {
		File lFile = new File("testFilesystem/DigestableLocationTest/test1");
		DigestableLocation l = DigestableLocation.create(lFile);
		
		DigestableLocation l1 = l.getLocation("location1");
		MetaEntityResource l1Meta = l1.getMetadata(MetaEntityResource.class);
		assertThat(l1Meta.getKeyType()).isEqualTo(String.class);
		assertThat(l1Meta.getEntityType()).isEqualTo(SimpleEntity.class);
		assertThat(l1Meta.getEntityName()).isEqualTo("LOCATION_1");
		DigestableLocation l2 = l.getLocation("location2");
		MetaEntityResource l2Meta = l2.getMetadata(MetaEntityResource.class);
		assertThat(l2Meta.getKeyType()).isEqualTo(String.class);
		assertThat(l2Meta.getEntityType()).isEqualTo(SimpleEntity.class);
		assertThat(l2Meta.getEntityName()).isEqualTo("LOCATION_2");
	}
	
	@Test
	void testSetMetadata() throws Exception {
		File lFile = new File("testFilesystem/DigestableLocationTest/test2");
		FileUtils.cleanDirectory(lFile);
		DigestableLocation l = DigestableLocation.create(lFile);
		
		File metadataFile = FileUtils.getFile(lFile, "__meta__.yml");
		assertThat(metadataFile.exists()).isFalse();
		
		MetaEntityResource meta = new MetaEntityResource();
		meta.setDatafileExtension("yml");
		meta.setKeyType(String.class);
		meta.setEntityType(SimpleEntity.class);
		meta.setPrettyPrint(false);
		meta.setEntityName("TEST_NAME");
		
		l.setMetadata(meta);
		
		assertThat(metadataFile.exists()).isTrue();
		
		MetaEntityResource updatedMeta = l.getMetadata(MetaEntityResource.class);
		
		assertThat(updatedMeta).isEqualTo(meta);
	}
	
	@Test
	void testKeys() throws Exception {
		File lFile = new File("testFilesystem/DigestableLocationTest/test1");
		DigestableLocation l = DigestableLocation.create(lFile);
		DigestableLocation l1 = l.getLocation("location1");
		DigestableLocation l2 = l.getLocation("location2");
		
		assertThat(l.keys().size()).isEqualTo(0);
		assertThat(l1.keys().size()).isEqualTo(2);
		assertThat(l1.keys()).contains("BarneyRubble");
		assertThat(l1.keys()).contains("FredFlintstone");
		assertThat(l2.keys().size()).isEqualTo(2);
		assertThat(l2.keys()).contains("BarneyFlintstone");
		assertThat(l2.keys()).contains("FredRubble");
	}
	
	@Test
	void testGetResource() throws Exception {
		File lFile = new File("testFilesystem/DigestableLocationTest/test1");
		DigestableLocation l = DigestableLocation.create(lFile);
		DigestableLocation l1 = l.getLocation("location1");
		
		assertThat(l1.getResource("BarneyRubble")).isNotNull();
		assertThat(l1.getResource("BarneyRubble").getDatafile())
				.isEqualTo(FileUtils.getFile(lFile, "location1", "BarneyRubble.json"));
	}
	
	@Test
	void testGetResourceThrowsMissingKeyError() throws Exception {
		File lFile = new File("testFilesystem/DigestableLocationTest/test1");
		DigestableLocation l = DigestableLocation.create(lFile);

		assertThatThrownBy(() -> l.getResource("XXXX")).isInstanceOf(MissingKeyError.class);
	}
	
	@Test
	void testCreateResource() throws Exception {
		File lFile = new File("testFilesystem/DigestableLocationTest/test2");
		FileUtils.cleanDirectory(lFile);
		DigestableLocation l = DigestableLocation.create(lFile);
		
		File newResourceFile = FileUtils.getFile(lFile, "NEW_RESOURCE.json");
		assertThat(newResourceFile.exists()).isFalse();
		
		DigestableResource newResource = l.createResource("NEW_RESOURCE");

		assertThat(newResourceFile.exists()).isTrue();
		assertThat(newResource.getDatafile()).isEqualTo(newResourceFile);

	}
	
	@Test
	void testCreateResourceThrowsDuplicateKeyError() throws Exception {
		File lFile = new File("testFilesystem/DigestableLocationTest/test2");
		FileUtils.cleanDirectory(lFile);
		DigestableLocation l = DigestableLocation.create(lFile);
		
		File newResourceFile = FileUtils.getFile(lFile, "NEW_RESOURCE.json");
		
		l.createResource("NEW_RESOURCE");

		assertThatThrownBy(() -> l.createResource("NEW_RESOURCE")).isInstanceOf(DuplicateKeyError.class);

	}
	
	@Test
	void testRemoveResource() throws Exception {
		File lFile = new File("testFilesystem/DigestableLocationTest/test2");
		FileUtils.cleanDirectory(lFile);
		DigestableLocation l = DigestableLocation.create(lFile);
		
		DigestableResource resource = l.createResource("NEW_RESOURCE");
		assertThat(resource.getDatafile().exists()).isTrue();
		
		DigestableResource removedResource = l.removeResource("NEW_RESOURCE");
		assertThat(removedResource.getDatafile().exists()).isFalse();
		assertThat(resource.getDatafile().exists()).isFalse();
		assertThat(removedResource.getDatafile()).isEqualTo(resource.getDatafile());
	}
	
	@Test
	void testRemoveResourceThrowsMissingKeyError() throws Exception {
		File lFile = new File("testFilesystem/DigestableLocationTest/test2");
		FileUtils.cleanDirectory(lFile);
		DigestableLocation l = DigestableLocation.create(lFile);
		assertThatThrownBy(() -> l.removeResource("NEW_RESOURCE")).isInstanceOf(MissingKeyError.class);
	}
	
	@Test
	void testResourceExists() throws Exception {
		File lFile = new File("testFilesystem/DigestableLocationTest/test1");
		DigestableLocation l = DigestableLocation.create(lFile);
		DigestableLocation l1 = l.getLocation("location1");
		
		assertThat(l1.resourceExists("BarneyRubble")).isTrue();
		assertThat(l1.resourceExists("XXXX")).isFalse();
	}
	
	@Test
	void testLocations() throws Exception {
		File lFile = new File("testFilesystem/DigestableLocationTest/test1");
		DigestableLocation l = DigestableLocation.create(lFile);
		assertThat(l.locations().size()).isEqualTo(2);
		assertThat(l.locations()).contains("location1");
		assertThat(l.locations()).contains("location2");
	}
	
	@Test
	void testGetLocation() throws Exception {
		File lFile = new File("testFilesystem/DigestableLocationTest/test1");
		DigestableLocation l = DigestableLocation.create(lFile);
		DigestableLocation l1 = l.getLocation("location1");
		
		assertThat(l1).isNotNull();
		assertThat(l1.getLocation()).isEqualTo(FileUtils.getFile(lFile, "location1"));
	}
	
	@Test
	void testGetLocationThrowsMissingKeyError() throws Exception {
		File lFile = new File("testFilesystem/DigestableLocationTest/test1");
		DigestableLocation l = DigestableLocation.create(lFile);
		assertThatThrownBy(() -> l.getLocation("XXXX")).isInstanceOf(MissingKeyError.class);
	}
	
	@Test
	void testCreateLocation() throws Exception {
		File lFile = new File("testFilesystem/DigestableLocationTest/test2");
		FileUtils.cleanDirectory(lFile);
		DigestableLocation l = DigestableLocation.create(lFile);
		
		File newLocationFile = FileUtils.getFile(lFile, "NEW_LOCATION");
		assertThat(newLocationFile.exists()).isFalse();
		
		DigestableLocation newLocation = l.createLocation("NEW_LOCATION");

		assertThat(newLocationFile.exists()).isTrue();
		assertThat(newLocationFile.isDirectory()).isTrue();
		assertThat(newLocation.getLocation()).isEqualTo(newLocationFile);

	}
	
	@Test
	void testCreateLocationThrowsDuplicateKeyError() throws Exception {
		File lFile = new File("testFilesystem/DigestableLocationTest/test2");
		FileUtils.cleanDirectory(lFile);
		DigestableLocation l = DigestableLocation.create(lFile);
		
		DigestableLocation newLocation = l.createLocation("NEW_LOCATION");

		assertThatThrownBy(() -> l.createLocation("NEW_LOCATION")).isInstanceOf(DuplicateKeyError.class);

	}
	
	@Test
	void testCreateLocationWithMetadata() throws Exception {
		File lFile = new File("testFilesystem/DigestableLocationTest/test2");
		FileUtils.cleanDirectory(lFile);
		DigestableLocation l = DigestableLocation.create(lFile);
		
		MetaEntityResource meta = new MetaEntityResource();
		meta.setDatafileExtension("yml");
		meta.setKeyType(String.class);
		meta.setEntityType(SimpleEntity.class);
		meta.setPrettyPrint(false);
		meta.setEntityName("TEST_NAME");
		
		File newLocationFile = FileUtils.getFile(lFile, "NEW_LOCATION");
		assertThat(newLocationFile.exists()).isFalse();
		
		DigestableLocation newLocation = l.createLocation("NEW_LOCATION", meta);

		assertThat(newLocationFile.exists()).isTrue();
		assertThat(newLocationFile.isDirectory()).isTrue();
		assertThat(newLocation.getLocation()).isEqualTo(newLocationFile);
		assertThat(newLocation.getMetadata(MetaEntityResource.class)).isEqualTo(meta);

	}
	
	@Test
	void testCreateLocationWithMetadataThrowsDuplicateKeyError() throws Exception {
		File lFile = new File("testFilesystem/DigestableLocationTest/test2");
		FileUtils.cleanDirectory(lFile);
		DigestableLocation l = DigestableLocation.create(lFile);
		
		MetaEntityResource meta = new MetaEntityResource();
		meta.setDatafileExtension("yml");
		meta.setKeyType(String.class);
		meta.setEntityType(SimpleEntity.class);
		meta.setPrettyPrint(false);
		meta.setEntityName("TEST_NAME");
		
		l.createLocation("NEW_LOCATION");
		
		assertThatThrownBy(() -> l.createLocation("NEW_LOCATION", meta))
				.isInstanceOf(DuplicateKeyError.class);
	}
	
	@Test
	void testRemoveLocation() throws Exception {
		File lFile = new File("testFilesystem/DigestableLocationTest/test2");
		FileUtils.cleanDirectory(lFile);
		DigestableLocation l = DigestableLocation.create(lFile);
		
		File locationFile = FileUtils.getFile(lFile, "NEW_LOCATION");
		
		DigestableLocation location = l.createLocation("NEW_LOCATION");

		assertThat(locationFile.exists()).isTrue();
		
		DigestableLocation removedLocation = l.removeLocation("NEW_LOCATION");
		assertThat(removedLocation).isEqualTo(location);
		assertThat(locationFile.exists()).isFalse();
	}
	
	@Test
	void testRemoveLocationThrowsMissingKeyError() throws Exception {
		File lFile = new File("testFilesystem/DigestableLocationTest/test2");
		FileUtils.cleanDirectory(lFile);
		DigestableLocation l = DigestableLocation.create(lFile);
		
		assertThatThrownBy(() -> l.removeLocation("XXXX")).isInstanceOf(MissingKeyError.class);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
