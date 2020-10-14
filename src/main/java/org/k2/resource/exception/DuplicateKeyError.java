package org.k2.resource.exception;

import java.text.MessageFormat;

public class DuplicateKeyError extends AbstractKeyError {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 702782057535453454L;

	private static String buildMessage(Class<?> type, String key) {
		return MessageFormat.format("Duplcate key detected for type: {0} key: {1}", type.getSimpleName(), key);
	}

	public DuplicateKeyError(Class<?> type, String key) {
		super(type, key, buildMessage(type, key));
	}

	public DuplicateKeyError(Class<?> type, String key, Throwable cause) {
		super(type, key, buildMessage(type, key), cause);
	}

	public DuplicateKeyError(Class<?> type, String key, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(type, key, buildMessage(type, key), cause, enableSuppression, writableStackTrace);
	}

}
