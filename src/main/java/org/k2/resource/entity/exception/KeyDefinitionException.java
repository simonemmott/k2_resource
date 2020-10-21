package org.k2.resource.entity.exception;

public class KeyDefinitionException extends EntityConfigurationException {

	public KeyDefinitionException(Class<?> entityType) {
		super(entityType, "Key definition exception on entity: "+entityType.getName());
	}

	public KeyDefinitionException(Class<?> entityType, String msg, Throwable err) {
		super(entityType, "Key definition exception on entity: "+entityType.getName()+" - "+msg, err);
	}

	public KeyDefinitionException(Class<?> entityType, String msg) {
		super(entityType, "Key definition exception on entity: "+entityType.getName()+" - "+msg);
	}

	public KeyDefinitionException(Class<?> entityType, Throwable err) {
		super(entityType, "Key definition exception on entity: "+entityType.getName(), err);
	}

}
