package org.k2.resource.exception;

import java.text.MessageFormat;

public class MissingKeyError extends AbstractKeyError {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2180766204579405116L;

	private static String buildMessage(Class<?> type, String key) {
		return MessageFormat.format("No key: {0} exists for type: {1}", key, type.getSimpleName());
	}

	public MissingKeyError(Class<?> type, String key) {
		super(type, key, buildMessage(type, key));
	}

	public MissingKeyError(Class<?> type, String key, Throwable cause) {
		super(type, key, buildMessage(type, key), cause);
	}

	public MissingKeyError(Class<?> type, String key, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(type, key, buildMessage(type, key), cause, enableSuppression, writableStackTrace);
	}

}
