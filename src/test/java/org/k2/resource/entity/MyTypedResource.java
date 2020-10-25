package org.k2.resource.entity;

import java.io.File;
import java.util.zip.Checksum;

import org.k2.resource.binary.exception.BinaryResourceInitializeException;
import org.k2.resource.entity.exception.EntityConfigurationException;
import org.k2.resource.entity.serialize.DefaultEntitySerializationFactory;

public class MyTypedResource extends EntityResource<String, String>{
	
	private static ThreadLocal<Checksum> checksum = new ThreadLocal<Checksum>();

	public MyTypedResource() throws BinaryResourceInitializeException, EntityConfigurationException {
		super(
				String.class, 
				String.class, 
				new File(""), 
				checksum, 
				new DefaultEntitySerializationFactory(String.class, String.class));
	}

}
