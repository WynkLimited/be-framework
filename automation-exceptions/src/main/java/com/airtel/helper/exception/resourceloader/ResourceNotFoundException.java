package com.airtel.helper.exception.resourceloader;

import com.airtel.helper.exception.ErrorCode;

/**
 * Thrown when a configuration resource/file cannot be found or read.
 */
public class ResourceNotFoundException extends ConfigServiceException {

	private static final long serialVersionUID = 3L;

	public ResourceNotFoundException() {
		super(ErrorCode.RESOURCE_NOT_FOUND.getDefaultMessage(), ErrorCode.RESOURCE_NOT_FOUND);
	}

	public ResourceNotFoundException(String message) {
		super(message, ErrorCode.RESOURCE_NOT_FOUND);
	}

	public ResourceNotFoundException(Exception exception) {
		super(exception != null ? exception.getMessage() : ErrorCode.RESOURCE_NOT_FOUND.getDefaultMessage(),
				ErrorCode.RESOURCE_NOT_FOUND, exception);
	}

	public ResourceNotFoundException(String message, Throwable cause) {
		super(message, ErrorCode.RESOURCE_NOT_FOUND, cause);
	}
}
