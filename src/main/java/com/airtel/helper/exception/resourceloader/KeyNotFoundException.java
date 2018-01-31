package com.airtel.helper.exception.resourceloader;

/**
 *
 * Exception is thrown when the key is not found in the config file. 
 */
public class KeyNotFoundException extends ConfigServiceException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8841952187180709094L;

	/**
	 * Constructor
	 */
	public KeyNotFoundException(){
		super();   
	}
	
	/**
	 * Constructor with error message
	 * @param message Error message
	 */
	public KeyNotFoundException(String message){
		super(message);   
	}
	
	/**
	 * Constructor with Exception object
	 * @param exception Exception object
	 */
	public KeyNotFoundException(Exception exception){
		super(exception);   
	}
	
}
