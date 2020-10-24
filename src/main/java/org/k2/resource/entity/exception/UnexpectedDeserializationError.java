package org.k2.resource.entity.exception;

import lombok.Getter;

public class UnexpectedDeserializationError extends RuntimeException {
	@Getter
	private final Class<?> entityType;

	public UnexpectedDeserializationError(Class<?> entityType) {
		super("Unexpected deerialization error for type: "+ entityType.getName());
		this.entityType = entityType;
	}

	public UnexpectedDeserializationError(Class<?> entityType, String message) {
		super("Unexpected deserialization error for type: "+ entityType.getName()+" - "+message);
		this.entityType = entityType;
	}

	public UnexpectedDeserializationError(Class<?> entityType, Throwable cause) {
		super("Unexpected deserialization error for type: "+ entityType.getName(), cause);
		this.entityType = entityType;
	}

	public UnexpectedDeserializationError(Class<?> entityType, String message, Throwable cause) {
		super("Unexpected deserialization error for type: "+ entityType.getName()+" - "+message, cause);
		this.entityType = entityType;
	}

	public UnexpectedDeserializationError(Class<?> entityType, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super("Unexpected deserialization error for type: "+ entityType.getName()+" - "+message, cause, enableSuppression, writableStackTrace);
		this.entityType = entityType;
	}

}
