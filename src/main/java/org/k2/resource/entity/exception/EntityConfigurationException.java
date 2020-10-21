package org.k2.resource.entity.exception;

import lombok.Getter;

@Getter
public class EntityConfigurationException extends Exception {
	
	private final Class<?> entityType;
	
	public EntityConfigurationException(Class<?> entityType) {
		super("Unknown enitty configuration exception");
		this.entityType = entityType;
	}

	public EntityConfigurationException(Class<?> entityType, String msg) {
		super(msg);
		this.entityType = entityType;
	}

	public EntityConfigurationException(Class<?> entityType, Throwable err) {
		super("Unknown enitty configuration exception", err);
		this.entityType = entityType;
	}

	public EntityConfigurationException(Class<?> entityType, String msg, Throwable err) {
		super(msg, err);
		this.entityType = entityType;
	}

}
