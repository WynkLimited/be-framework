package com.airtel.helper.exception;

/**
 * Stable error codes for framework exceptions.
 */
public enum ErrorCode {

	CONFIGURATION_PROPERTY("ERR_CFG_00", "Invalid or missing configuration property"),
	RESOURCE_NOT_FOUND("ERR_CFG_01", "Configuration resource not found"),
	EMPTY_CONFIG_FILE("ERR_CFG_02", "Configuration file is empty"),
	KEY_NOT_FOUND("ERR_CFG_03", "Configuration key not found"),
	GENERIC("ERR_GEN_00", "Unexpected framework error");

	private final String code;
	private final String defaultMessage;

	ErrorCode(String code, String defaultMessage) {
		this.code = code;
		this.defaultMessage = defaultMessage;
	}

	public String getCode() {
		return code;
	}

	public String getDefaultMessage() {
		return defaultMessage;
	}

	@Override
	public String toString() {
		return code;
	}
}
