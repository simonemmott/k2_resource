package org.k2.resource.exception;

import lombok.Getter;

public abstract class AbstractKeyError extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4019872818801487547L;
	@Getter
	private final String key;

	public AbstractKeyError(String key, String message) {
		super(message);
		this.key = key;
	}

	public AbstractKeyError(String key, String message, Throwable cause) {
		super(message, cause);
		this.key = key;
	}

	public AbstractKeyError(String key, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		this.key = key;
	}

}
