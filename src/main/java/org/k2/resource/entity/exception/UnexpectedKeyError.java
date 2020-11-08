package org.k2.resource.entity.exception;

public class UnexpectedKeyError extends RuntimeException {

	public UnexpectedKeyError() {
	}

	public UnexpectedKeyError(String message) {
		super(message);
	}

	public UnexpectedKeyError(Throwable cause) {
		super(cause);
	}

	public UnexpectedKeyError(String message, Throwable cause) {
		super(message, cause);
	}

	public UnexpectedKeyError(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
