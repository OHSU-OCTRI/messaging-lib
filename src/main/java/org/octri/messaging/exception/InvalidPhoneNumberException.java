package org.octri.messaging.exception;

/**
 * Thrown when a phone number string is not in one of the accepted formats.
 */
public class InvalidPhoneNumberException extends RuntimeException {

	/**
	 * Constructs an exception with a custom error message.
	 * 
	 * @param message
	 *            custom error message
	 */
	public InvalidPhoneNumberException(String message) {
		super(message);
	}

	/**
	 * Constructs an exception with a custom error message and cause.
	 *
	 * @param message
	 *            custom error message
	 * @param cause
	 *            the exception that caused the new exception to be thrown
	 */
	public InvalidPhoneNumberException(String message, Throwable cause) {
		super(message, cause);
	}

}
