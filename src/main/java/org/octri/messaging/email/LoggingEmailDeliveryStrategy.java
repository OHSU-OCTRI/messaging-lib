package org.octri.messaging.email;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Mock email delivery strategy for development environments that logs messages
 * to the console for inspection.
 */
public class LoggingEmailDeliveryStrategy implements EmailDeliveryStrategy {

	private static final Logger log = LoggerFactory.getLogger(LoggingEmailDeliveryStrategy.class);

	/**
	 * Logs the message to the console for inspection.
	 */
	@Override
	public Optional<String> sendEmail(String fromEmail, String toEmail, String messageSubject, String messageText) {
		log.info("Mock email from {} to {}", fromEmail, toEmail);
		log.info("Mock message subject: " + messageSubject);
		log.info("Mock message text:\n" + messageText);
		return Optional.empty();
	}

}
