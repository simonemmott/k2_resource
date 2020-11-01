package org.k2.resource.entity.managed;

import static org.assertj.core.api.Assertions.*;

import java.io.File;

import org.junit.jupiter.api.Test;
import org.k2.resource.exception.ResourceConfigurationException;

class ResourceManagerFactoryTest {

	@Test
	void testItShouldCreateADefaultResourceManager() throws Exception {
		File resourcesDir = new File("testFilesystem/EntityResourceTest/ManagedEntityResourceTest/resource1");
		ResourceManagerFactory factory = new ResourceManagerFactory();
		
		ResourceManager rm = factory.create(resourcesDir);
		
		assertThat(rm).isNotNull();
	}
	
	@Test
	void testItShouldThrowResourceConfigurationExceptionIfTheResourcesDirDoesNotExist() {
		File resourcesDir = new File("testFilesystem/EntityResourceTest/ManagedEntityResourceTest/XXXX");
		ResourceManagerFactory factory = new ResourceManagerFactory();
		
		assertThatThrownBy(() -> factory.create(resourcesDir))
				.isInstanceOf(ResourceConfigurationException.class);
		
	}

}
