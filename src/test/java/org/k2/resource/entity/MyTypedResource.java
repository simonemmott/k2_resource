package org.k2.resource.entity;

import java.io.File;
import java.util.zip.Checksum;

import org.k2.resource.binary.exception.BinaryResourceInitializeException;
import org.k2.resource.entity.exception.EntityConfigurationException;
import org.k2.resource.entity.serialize.DefaultEntitySerializationFactory;

public class MyTypedResource extends GenericEntityResource<String, String>{
	
	public MyTypedResource() throws BinaryResourceInitializeException, EntityConfigurationException {
		super(
				String.class, 
				String.class, 
				new File(""));
	}

}
