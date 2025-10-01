package org.octri.messaging.email;

import java.util.Optional;

import org.octri.messaging.exception.UnsuccessfulDeliveryException;

/**
 * Interface for email message delivery services.
 */
public interface EmailDeliveryStrategy {

	/**
	 * Sends an email message. Implementations may optionally return a string representation of the delivery details.
	 * Implementations should throw {@link UnsuccessfulDeliveryException} when delivery is unsuccessful to allow callers
	 * to respond to delivery failure.
	 *
	 * @param fromEmail
	 *            sender email address
	 * @param toEmail
	 *            recipient email address
	 * @param messageSubject
	 *            subject of the message
	 * @param messageText
	 *            body text of the message
	 * @return optional string representation of delivery details, e.g. API response
	 *         from a transactional mail service
	 * @throws UnsuccessfulDeliveryException
	 *             delivery failure details
	 */
	public Optional<String> sendEmail(String fromEmail, String toEmail, String messageSubject, String messageText);

	/**
	 * Sends an email message from the default sender address. Implementations may optionally return a string
	 * representation of the delivery details. Implementations should throw {@link UnsuccessfulDeliveryException} when
	 * delivery is unsuccessful to allow callers to respond to delivery failure.
	 *
	 * @param toEmail
	 *            recipient email address
	 * @param messageSubject
	 *            subject of the message
	 * @param messageText
	 *            body text of the message
	 * @return optional string representation of the delivery details, e.g. API response from a transactional mail
	 *         service
	 * @throws UnsuccessfulDeliveryException
	 *             delivery failure details
	 */
	public Optional<String> sendEmail(String toEmail, String messageSubject, String messageText);
}
