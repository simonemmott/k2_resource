package org.k2.resource.exception;

import java.text.MessageFormat;

public class EntityLockedError extends AbstractKeyError {
	
	private static String buildMessage(String key) {
		return MessageFormat.format("Unable to lock entity with key: {0}", key);
	}

	public EntityLockedError(String key) {
		super(key, buildMessage(key));
	}

	public EntityLockedError(String key, Throwable cause) {
		super(key, buildMessage(key), cause);
	}

	public EntityLockedError(String key, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(key, buildMessage(key), cause, enableSuppression, writableStackTrace);
	}

	public EntityLockedError(String key, String msg) {
		super(key, buildMessage(key)+" - "+msg);
	}

	public EntityLockedError(String key, String msg, Throwable cause) {
		super(key, buildMessage(key)+" - "+msg, cause);
	}

	public EntityLockedError(String key, String msg, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(key, buildMessage(key)+" - "+msg, cause, enableSuppression, writableStackTrace);
	}

}
