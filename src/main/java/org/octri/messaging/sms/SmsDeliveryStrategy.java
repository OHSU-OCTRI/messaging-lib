package org.octri.messaging.sms;

import java.util.Optional;

import org.octri.messaging.exception.UnsuccessfulDeliveryException;

/**
 * Interface for SMS message delivery services.
 */
public interface SmsDeliveryStrategy {

	/**
	 * Sends an SMS message. Implementations may optionally return a string representation of the delivery details.
	 * Implementations should throw {@link UnsuccessfulDeliveryException} when delivery is unsuccessful to allow callers
	 * to respond to delivery failure.
	 *
	 * @param fromNumber
	 *            SMS sender phone number
	 * @param toNumber
	 *            SMS recipient phone number
	 * @param messageText
	 *            body text of the message
	 * @return optional string representation of delivery details, e.g. Twilio API
	 *         response
	 * @throws UnsuccessfulDeliveryException
	 *             delivery failure details
	 */
	public Optional<String> sendSms(String fromNumber, String toNumber, String messageText);

}
