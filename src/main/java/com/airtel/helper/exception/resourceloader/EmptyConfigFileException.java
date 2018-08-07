
package com.airtel.helper.exception.resourceloader;

/**
 *
 * Exception is thrown when the config file if found to be empty. 
 */
public class EmptyConfigFileException extends ConfigServiceException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3053351086886156666L;

	/**
	 * Constructor 
	 */
	public EmptyConfigFileException(){
		super();   
	}
	
	/**
	 * Constructor with error message
	 * @param message Error message
	 */
	public EmptyConfigFileException(String message){
		super(message);   
	}
	
	/**
	 * Constructor with Exception object
	 * @param exception Exception object
	 */
	public EmptyConfigFileException(Exception exception){
		super(exception);   
	}
	
	
}
