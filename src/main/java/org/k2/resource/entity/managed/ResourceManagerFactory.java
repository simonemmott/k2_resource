package org.k2.resource.entity.managed;

import java.io.File;

import org.k2.resource.exception.ResourceConfigurationException;

public class ResourceManagerFactory {

	public ResourceManagerFactory() {
	}

	public ResourceManager create(File resourcesDir) throws ResourceConfigurationException {
		return new SimpleResourceManager(resourcesDir);
	}

}
