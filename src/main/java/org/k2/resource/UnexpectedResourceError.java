package org.k2.resource;

public class UnexpectedResourceError extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7005041544207466988L;

	public UnexpectedResourceError(String message) {
		super(message);
	}

	public UnexpectedResourceError(Throwable cause) {
		super(cause);
	}

	public UnexpectedResourceError(String message, Throwable cause) {
		super(message, cause);
	}

	public UnexpectedResourceError(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
