
package com.airtel.common.helper.exception.resourceloader;

import com.airtel.common.helper.exception.GenericException;


/**
 * Base exception for ConfigService
 */
public class ConfigServiceException extends GenericException{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4201046007684924915L;

	/**
	 * Constructor
	 *
	 */
	public ConfigServiceException(){
		super();   
	}
	
	/**
	 * Constructor with error message
	 * @param message Error message
	 */
	public ConfigServiceException(String message){
		super(message);   
	}
	
	/**
	 * Constructor with Exception object
	 * @param exception Exception object
	 */
	public ConfigServiceException(Exception exception){
		super(exception);   
	}
	
	public ConfigServiceException(String message, String errorCode){
		super(message, errorCode);  
	}
	
}
