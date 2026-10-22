package com.airtel.helper.exception.resourceloader;

import com.airtel.helper.exception.ErrorCode;
import com.airtel.helper.exception.GenericException;

/**
 * Base exception for configuration / resource-loader failures.
 */
public class ConfigServiceException extends GenericException {

	private static final long serialVersionUID = 2L;

	public ConfigServiceException() {
		super();
	}

	public ConfigServiceException(String message) {
		super(message);
	}

	public ConfigServiceException(Throwable cause) {
		super(cause);
	}

	public ConfigServiceException(Exception exception) {
		super(exception);
	}

	public ConfigServiceException(String message, String errorCode) {
		super(message, errorCode);
	}

	public ConfigServiceException(String message, ErrorCode errorCode) {
		super(message, errorCode);
	}

	public ConfigServiceException(String message, ErrorCode errorCode, Throwable cause) {
		super(message, errorCode, cause);
	}
}
