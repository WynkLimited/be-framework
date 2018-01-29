package com.airtel.common.helper.exception.resourceloader;

/**
 *
 * Exception is thrown when the config file is not found. 
 */
public class ResourceNotFoundException extends ConfigServiceException{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1014994692016709995L;

	/**
	 * Constructor
	 */
	public ResourceNotFoundException(){
		super();   
	}
	
	/**
	 * Constructor with error message
	 * @param message Error message
	 */
	public ResourceNotFoundException(String message){
		super(message);   
	}
	
	/**
	 * Constructor with Exception object
	 * @param exception Exception object
	 */
	public ResourceNotFoundException(Exception exception){
		super(exception);   
	}
	
}
