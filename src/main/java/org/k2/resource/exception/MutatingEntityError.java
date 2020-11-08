package org.k2.resource.exception;

import java.text.MessageFormat;

public class MutatingEntityError extends AbstractKeyError {
	
	private static String buildMessage(String key) {
		return MessageFormat.format("The entity with key: {0} has already been updated.", key);
	}

	public MutatingEntityError(String key) {
		super(key, buildMessage(key));
	}

	public MutatingEntityError(String key, Throwable cause) {
		super(key, buildMessage(key), cause);
	}

	public MutatingEntityError(String key, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(key, buildMessage(key), cause, enableSuppression, writableStackTrace);
	}

	public MutatingEntityError(String key, String msg) {
		super(key, buildMessage(key)+" - "+msg);
	}

	public MutatingEntityError(String key, String msg, Throwable cause) {
		super(key, buildMessage(key)+" - "+msg, cause);
	}

	public MutatingEntityError(String key, String msg, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(key, buildMessage(key)+" - "+msg, cause, enableSuppression, writableStackTrace);
	}

}
