package org.k2.resource.entity.exception;

public class ManagedResourceInitializationError extends ManagedResourceError {

	public ManagedResourceInitializationError() {
		super("Unknown managed resource initialization error.");
	}

	public ManagedResourceInitializationError(String message) {
		super("Managed resource initialization error - " + message);
	}

	public ManagedResourceInitializationError(Throwable cause) {
		super("Unknown managed resource initialization error.", cause);
	}

	public ManagedResourceInitializationError(String message, Throwable cause) {
		super("Managed resource initialization error - " + message, cause);
	}

	public ManagedResourceInitializationError(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super("Managed resource initialization error - " + message, cause, enableSuppression, writableStackTrace);
	}

}
