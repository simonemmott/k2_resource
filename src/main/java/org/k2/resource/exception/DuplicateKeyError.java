package org.k2.resource.exception;

import java.text.MessageFormat;

public class DuplicateKeyError extends AbstractKeyError {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 702782057535453454L;

	private static String buildMessage(String key) {
		return MessageFormat.format("Duplcate key detected for type: {0}", key);
	}

	public DuplicateKeyError(String key) {
		super(key, buildMessage(key));
	}

	public DuplicateKeyError(String key, Throwable cause) {
		super(key, buildMessage(key), cause);
	}

	public DuplicateKeyError(String key, String message) {
		super(key, buildMessage(key)+" - "+message);
	}

	public DuplicateKeyError(String key, String message, Throwable cause) {
		super(key, buildMessage(key)+" - "+message, cause);
	}

	public DuplicateKeyError(String key, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(key, buildMessage(key), cause, enableSuppression, writableStackTrace);
	}

}
