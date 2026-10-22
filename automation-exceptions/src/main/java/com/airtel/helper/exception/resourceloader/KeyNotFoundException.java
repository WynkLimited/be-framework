package com.airtel.helper.exception.resourceloader;

import com.airtel.helper.exception.ErrorCode;

/**
 * Thrown when a required key is missing from a configuration file.
 */
public class KeyNotFoundException extends ConfigServiceException {

	private static final long serialVersionUID = 5L;

	public KeyNotFoundException() {
		super(ErrorCode.KEY_NOT_FOUND.getDefaultMessage(), ErrorCode.KEY_NOT_FOUND);
	}

	public KeyNotFoundException(String message) {
		super(message, ErrorCode.KEY_NOT_FOUND);
	}

	public KeyNotFoundException(Exception exception) {
		super(exception != null ? exception.getMessage() : ErrorCode.KEY_NOT_FOUND.getDefaultMessage(),
				ErrorCode.KEY_NOT_FOUND, exception);
	}

	public KeyNotFoundException(String message, Throwable cause) {
		super(message, ErrorCode.KEY_NOT_FOUND, cause);
	}
}
