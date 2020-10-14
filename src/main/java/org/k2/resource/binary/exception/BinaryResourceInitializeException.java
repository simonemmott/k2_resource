package org.k2.resource.binary.exception;

import java.io.File;

public class BinaryResourceInitializeException extends BinaryResourceException {

	public BinaryResourceInitializeException(String message, File dir) {
		super(message, dir);
		// TODO Auto-generated constructor stub
	}

	public BinaryResourceInitializeException(String message, File dir, Throwable cause) {
		super(message, dir, cause);
		// TODO Auto-generated constructor stub
	}

}
