package org.octri.messaging.exception;

/**
 * Thrown when the message delivery service is unable to deliver a message. The
 * exception may optionally include a detailed error response, such as a stringified
 * API payload.
 */
public class UnsuccessfulDeliveryException extends RuntimeException {

	/**
	 * Optional string representation of the error that triggered the exception.
	 */
	private String errorResponse;

	/**
	 * Constructs an exception with a custom error message.
	 *
	 * @param message
	 *            custom error message
	 */
	public UnsuccessfulDeliveryException(String message) {
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
	public UnsuccessfulDeliveryException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs an exception with a custom error message, cause, and error response string.
	 *
	 * @param message
	 *            custom error message
	 * @param cause
	 *            the exception that caused the new exception to be thrown
	 * @param errorResponse
	 *            string representation of the error returned by the delivery system (e.g. API response JSON)
	 */
	public UnsuccessfulDeliveryException(String message, Throwable cause, String errorResponse) {
		super(message, cause);
		this.errorResponse = errorResponse;
	}

	/**
	 * Gets the detailed error response when present. If not present, the exception
	 * message is returned.
	 *
	 * @return the detailed error response if present, otherwise the exception
	 *         message
	 */
	public String getErrorResponse() {
		return errorResponse != null ? errorResponse : getMessage();
	}

}
