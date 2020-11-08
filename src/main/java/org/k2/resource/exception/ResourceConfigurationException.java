package org.k2.resource.exception;

import java.io.File;

public class ResourceConfigurationException extends Exception {

	public ResourceConfigurationException(File dir) {
		super("Unknown configuration exception at: " + dir.getAbsolutePath());
	}

	public ResourceConfigurationException(File dir, String message) {
		super("Configuration exception at: " + dir.getAbsolutePath() + " - " + message);
		// TODO Auto-generated constructor stub
	}

	public ResourceConfigurationException(File dir, Throwable cause) {
		super("Unknown configuration exception at: "+ dir.getAbsolutePath(), cause);
		// TODO Auto-generated constructor stub
	}

	public ResourceConfigurationException(File dir, String message, Throwable cause) {
		super("Configuration exception at: " + dir.getAbsolutePath() + " - " + message, cause);
		// TODO Auto-generated constructor stub
	}

	public ResourceConfigurationException(File dir, String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super("Configuration exception at: " + dir.getAbsolutePath() + " - " + message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

}
