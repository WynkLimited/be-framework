package com.airtel.common.helper.exception;

public class GenericException extends Exception{
	
	private String errorCode;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4201046007684924915L;

	/**
	 * Constructor
	 *
	 */
	public GenericException(){
		super();   
	}
	
	/**
	 * Constructor with error message
	 * @param message Error message
	 */
	public GenericException(String message){
		super(message);   
	}
	
	/**
	 * Constructor with Exception object
	 * @param exception Exception object
	 */
	public GenericException(Exception exception){
		super(exception);   
	}

	public GenericException(String message, String errorCode) {
		super(message);
		this.errorCode = errorCode;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	
}
