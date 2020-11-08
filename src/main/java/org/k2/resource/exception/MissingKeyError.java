package org.k2.resource.exception;

import java.text.MessageFormat;

public class MissingKeyError extends AbstractKeyError {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2180766204579405116L;

	private static String buildMessage(String key) {
		return MessageFormat.format("Unable to identify resource for key: {0}", key);
	}

	public MissingKeyError(String key) {
		super(key, buildMessage(key));
	}

	public MissingKeyError(String key, Throwable cause) {
		super(key, buildMessage(key), cause);
	}

	public MissingKeyError(String key, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(key, buildMessage(key), cause, enableSuppression, writableStackTrace);
	}

	public MissingKeyError(String key, String msg) {
		super(key, buildMessage(key)+" - "+msg);
	}

	public MissingKeyError(String key, String msg, Throwable cause) {
		super(key, buildMessage(key)+" - "+msg, cause);
	}

	public MissingKeyError(String key, String msg, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(key, buildMessage(key)+" - "+msg, cause, enableSuppression, writableStackTrace);
	}

}
