package org.k2.resource.entity.exception;

import lombok.Getter;

public class UnexpectedSerializationError extends RuntimeException {
	@Getter
	private final Class<?> entityType;

	public UnexpectedSerializationError(Class<?> entityType) {
		super("Unexpected serialization error for type: "+ entityType.getName());
		this.entityType = entityType;
	}

	public UnexpectedSerializationError(Class<?> entityType, String message) {
		super("Unexpected serialization error for type: "+ entityType.getName()+" - "+message);
		this.entityType = entityType;
	}

	public UnexpectedSerializationError(Class<?> entityType, Throwable cause) {
		super("Unexpected serialization error for type: "+ entityType.getName(), cause);
		this.entityType = entityType;
	}

	public UnexpectedSerializationError(Class<?> entityType, String message, Throwable cause) {
		super("Unexpected serialization error for type: "+ entityType.getName()+" - "+message, cause);
		this.entityType = entityType;
	}

	public UnexpectedSerializationError(Class<?> entityType, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super("Unexpected serialization error for type: "+ entityType.getName()+" - "+message, cause, enableSuppression, writableStackTrace);
		this.entityType = entityType;
	}

}
