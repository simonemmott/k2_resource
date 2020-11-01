package org.k2.resource.entity.managed;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.powermock.reflect.Whitebox;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.k2.resource.entity.annotation.Key;
import org.k2.resource.entity.annotation.RefItem;
import org.k2.resource.entity.exception.EntityConfigurationException;
import org.k2.resource.exception.ResourceConfigurationException;
import org.mockito.BDDMockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;

@RunWith(PowerMockRunner.class)
@PrepareForTest(FileUtils.class)
class SimpleResourceManagerTest {
	
	@Getter
	@Setter
	@AllArgsConstructor
	@RefItem()
	class TestRefItem {		
		@Key
		private String key;
	}
	
	@Getter
	@Setter
	@AllArgsConstructor
	class TestItem {		
		@Key
		private String key;
	}	

	File mockDir() {
		File mockDir = mock(File.class);
		when(mockDir.exists()).thenReturn(true);
		when(mockDir.isDirectory()).thenReturn(true);
		when(mockDir.canRead()).thenReturn(true);
		when(mockDir.canWrite()).thenReturn(true);
		return mockDir;
	}

	@Test
	void testItShouldSetTheDirField() throws Exception {
		File mockDir = mockDir();
		SimpleResourceManager rm = new SimpleResourceManager(mockDir);
		File dir = Whitebox.getInternalState(rm, "dir");
		assertThat(dir).isEqualTo(mockDir);
	}
	
	@Test
	void testItShouldThrowResourceConfigurationExceptionIfTheResourcesDirDoesNotExist() {
		File mockDir =  mockDir();
		when(mockDir.exists()).thenReturn(false);

		assertThatThrownBy(() -> new SimpleResourceManager(mockDir))
				.isInstanceOf(ResourceConfigurationException.class);
		
	}

	@Test
	void testItShouldThrowResourceConfigurationExceptionIfTheResourcesDirIsNotADirectory() {
		File mockDir =  mockDir();
		when(mockDir.isDirectory()).thenReturn(false);

		assertThatThrownBy(() -> new SimpleResourceManager(mockDir))
				.isInstanceOf(ResourceConfigurationException.class);
		
	}

	@Test
	void testItShouldThrowResourceConfigurationExceptionIfTheResourcesDirIsNotReadable() {
		File mockDir =  mockDir();
		when(mockDir.canRead()).thenReturn(false);

		assertThatThrownBy(() -> new SimpleResourceManager(mockDir))
				.isInstanceOf(ResourceConfigurationException.class);
		
	}

	@Test
	void testItShouldThrowResourceConfigurationExceptionIfTheResourcesDirIsNotWritable() {
		File mockDir =  mockDir();
		when(mockDir.canWrite()).thenReturn(false);

		assertThatThrownBy(() -> new SimpleResourceManager(mockDir))
				.isInstanceOf(ResourceConfigurationException.class);
		
	}
	
	@Test
	void testManageShouldThrowEntityConfigurationExceptionIfTypeIsNotRefItem() throws Exception {
		File mockDir =  mockDir();
		SimpleResourceManager rm = new SimpleResourceManager(mockDir);
		
		assertThatThrownBy(() -> rm.manage(TestItem.class))
				.isInstanceOf(EntityConfigurationException.class);
	}
	
	@Test
	void testManageShouldCreateAResourceDirectoryIfItDoesNotExist() throws Exception {
		File mockDir =  mockDir();
		when(mockDir.getAbsolutePath()).thenReturn("ROOT");
		PowerMockito.doNothing().when(FileUtils.class, "forceMkdir", any(File.class));
		SimpleResourceManager rm = new SimpleResourceManager(mockDir);
		
		rm.manage(TestRefItem.class);
		
		fail("Not implemented");
		
		
	}

}








