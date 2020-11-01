package org.k2.resource.entity.managed;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.k2.resource.entity.exception.EntityConfigurationException;
import org.k2.resource.entity.util.RefItemUtils;
import org.k2.resource.exception.ResourceConfigurationException;

public class SimpleResourceManager implements ResourceManager {
	
	private final File dir;

	public SimpleResourceManager(File dir) throws ResourceConfigurationException {
		this.dir = dir;
		if (!dir.exists()) 
			throw new ResourceConfigurationException(dir, "Does not exist!");
		if (!dir.isDirectory()) 
			throw new ResourceConfigurationException(dir, "Is not a directory!");
		if (!dir.canRead()) 
			throw new ResourceConfigurationException(dir, "Cannot be opened for reading!");
		if (!dir.canWrite()) 
			throw new ResourceConfigurationException(dir, "Cannot be opened for writing!");
	}
	
	private File resourceDir(Class<?> refType) throws EntityConfigurationException {
		String typeReference = RefItemUtils.getTypeReference(refType);
		return new File(dir.getAbsoluteFile()+File.separator+typeReference);
	}
	
	void manage(Class<?> refType) throws EntityConfigurationException {
		RefItemUtils.checkIsRefItem(refType);
		File resourceDir = resourceDir(refType);
		if (!resourceDir.exists()) {
			try {
				FileUtils.forceMkdir(resourceDir);
			} catch (IOException e) {
				throw new EntityConfigurationException(
						refType, 
						"Unable to create resource directory: " + resourceDir.getAbsolutePath());
			}
		}
	}

}
