package org.k2.resource.exception;

import lombok.Getter;

public abstract class AbstractKeyError extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4019872818801487547L;
	@Getter
	private final Class<?> type;
	@Getter
	private final String key;

	public AbstractKeyError(Class<?> type, String key, String message) {
		super(message);
		this.type = type;
		this.key = key;
	}

	public AbstractKeyError(Class<?> type, String key, String message, Throwable cause) {
		super(message, cause);
		this.type = type;
		this.key = key;
	}

	public AbstractKeyError(Class<?> type, String key, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		this.type = type;
		this.key = key;
	}

}
