/**
 * 
 */
package org.alfresco.latch.exception;

/**
 * @author iarroyo
 *
 * @mail iarroyoescobar@gmail.com
 *
 */
public class LatchException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 
	 */
	public LatchException(String msg) {
		super(msg);
	}
	
	public LatchException(String msg, Throwable e) {
		super(msg, e);
	}
}
