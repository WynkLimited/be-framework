package com.airtel.helper.exception.resourceloader;

import com.airtel.helper.exception.ErrorCode;

/**
 * Thrown when a configuration file exists but has no usable content.
 */
public class EmptyConfigFileException extends ConfigServiceException {

	private static final long serialVersionUID = 4L;

	public EmptyConfigFileException() {
		super(ErrorCode.EMPTY_CONFIG_FILE.getDefaultMessage(), ErrorCode.EMPTY_CONFIG_FILE);
	}

	public EmptyConfigFileException(String message) {
		super(message, ErrorCode.EMPTY_CONFIG_FILE);
	}

	public EmptyConfigFileException(Exception exception) {
		super(exception != null ? exception.getMessage() : ErrorCode.EMPTY_CONFIG_FILE.getDefaultMessage(),
				ErrorCode.EMPTY_CONFIG_FILE, exception);
	}

	public EmptyConfigFileException(String message, Throwable cause) {
		super(message, ErrorCode.EMPTY_CONFIG_FILE, cause);
	}
}
