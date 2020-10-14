package org.k2.resource.exception;

import java.text.MessageFormat;

public class MutatingEntityError extends AbstractKeyError {
	
	private static String buildMessage(Class<?> type, String key) {
		return MessageFormat.format("The entity of type: {0} with key: {1} was already changed", type.getSimpleName(), key);
	}

	public MutatingEntityError(Class<?> type, String key) {
		super(type, key, buildMessage(type, key));
	}

	public MutatingEntityError(Class<?> type, String key, Throwable cause) {
		super(type, key, buildMessage(type, key), cause);
	}

	public MutatingEntityError(Class<?> type, String key, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(type, key, buildMessage(type, key), cause, enableSuppression, writableStackTrace);
	}

}
