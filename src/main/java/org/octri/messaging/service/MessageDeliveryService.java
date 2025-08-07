package org.octri.messaging.service;

import java.util.Optional;

import org.octri.messaging.email.EmailDeliveryStrategy;
import org.octri.messaging.email.NoopEmailDeliveryStrategy;
import org.octri.messaging.exception.UnsuccessfulDeliveryException;
import org.octri.messaging.sms.NoopSmsDeliveryStrategy;
import org.octri.messaging.sms.SmsDeliveryStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service for delivering messages.
 */
public class MessageDeliveryService {

	private static final Logger log = LoggerFactory.getLogger(MessageDeliveryService.class);

	private EmailDeliveryStrategy emailStrategy;
	private SmsDeliveryStrategy smsStrategy;

	/**
	 * Constructor.
	 *
	 * @param emailStrategy
	 *            the strategy to use to deliver email messages
	 * @param smsStrategy
	 *            the strategy to use to deliver SMS messages
	 */
	public MessageDeliveryService(EmailDeliveryStrategy emailStrategy, SmsDeliveryStrategy smsStrategy) {
		log.debug("Instantiating message delivery service.");
		log.debug("Email delivery strategy: " + emailStrategy);
		log.debug("SMS delivery strategy: " + smsStrategy);
		this.emailStrategy = emailStrategy;
		this.smsStrategy = smsStrategy;
	}

	/**
	 * Whether sending an email is enabled.
	 *
	 * @return true if email delivery is enabled
	 */
	public Boolean isEmailEnabled() {
		return !(emailStrategy instanceof NoopEmailDeliveryStrategy);
	}

	/**
	 * Whether sending an SMS is enabled.
	 *
	 * @return true if SMS message delivery is enabled
	 */
	public Boolean isSmsEnabled() {
		return !(smsStrategy instanceof NoopSmsDeliveryStrategy);
	}

	/**
	 * Sends an email message using the current delivery strategy.
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
	public Optional<String> sendEmail(String fromEmail, String toEmail, String messageSubject, String messageText) {
		return emailStrategy.sendEmail(fromEmail, toEmail, messageSubject, messageText);
	}

	/**
	 * Sends an SMS message using the current delivery strategy.
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
	public Optional<String> sendSms(String fromNumber, String toNumber, String messageText) {
		return smsStrategy.sendSms(fromNumber, toNumber, messageText);
	}

}
