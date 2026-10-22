package com.airtel.helper.exception;

/**
 * Base checked exception for the automation framework.
 */
public class GenericException extends Exception {

	private static final long serialVersionUID = 1L;

	private final String errorCode;

	public GenericException() {
		this(null, (String) null, null);
	}

	public GenericException(String message) {
		this(message, (String) null, null);
	}

	public GenericException(Throwable cause) {
		this(cause != null ? cause.getMessage() : null, (String) null, cause);
	}

	public GenericException(String message, String errorCode) {
		this(message, errorCode, null);
	}

	public GenericException(String message, ErrorCode errorCode) {
		this(message, errorCode != null ? errorCode.getCode() : null, null);
	}

	public GenericException(String message, Throwable cause) {
		this(message, (String) null, cause);
	}

	public GenericException(String message, ErrorCode errorCode, Throwable cause) {
		this(message, errorCode != null ? errorCode.getCode() : null, cause);
	}

	public GenericException(String message, String errorCode, Throwable cause) {
		super(resolveMessage(message, errorCode), cause);
		this.errorCode = errorCode;
	}

	public String getErrorCode() {
		return errorCode;
	}

	@Override
	public String toString() {
		if (errorCode == null || errorCode.isBlank()) {
			return super.toString();
		}
		return getClass().getName() + ": [" + errorCode + "] " + getLocalizedMessage();
	}

	private static String resolveMessage(String message, String errorCode) {
		if (message != null && !message.isBlank()) {
			return message;
		}
		if (errorCode != null && !errorCode.isBlank()) {
			return errorCode;
		}
		return null;
	}
}
