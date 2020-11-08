package org.k2.util.reflection.exception;

import lombok.Getter;

public class ReflectionError extends Exception {

	@Getter
	private final Class<?> type;
	public ReflectionError(Class<?> type) {
		super("Unknown reflection error on type: "+type.getName());
		this.type = type;
	}

	public ReflectionError(Class<?> type, String message) {
		super(message + " on type: "+type.getName());
		this.type = type;
	}

	public ReflectionError(Class<?> type, Throwable cause) {
		super("Unknown reflection error on type: "+type.getName(), cause);
		this.type = type;
	}

	public ReflectionError(Class<?> type, String message, Throwable cause) {
		super(message + " on type: "+type.getName(), cause);
		this.type = type;
	}

	public ReflectionError(Class<?> type, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message + " on type: "+type.getName(), cause, enableSuppression, writableStackTrace);
		this.type = type;
	}

}
