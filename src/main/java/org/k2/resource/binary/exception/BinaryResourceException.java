package org.k2.resource.binary.exception;

import java.io.File;

import lombok.Getter;

public class BinaryResourceException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4961357966627662368L;
	@Getter
	private final File dir;
		
	public BinaryResourceException(String message, File dir) {
		super(message);
		this.dir = dir;
	}

	public BinaryResourceException(String message, File dir, Throwable cause) {
		super(message, cause);
		this.dir = dir;
	}

}
