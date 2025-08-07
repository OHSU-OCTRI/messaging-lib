package org.octri.messaging.sms;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Mock SMS delivery strategy for development environments that logs messages to
 * the console for inspection.
 */
public class LoggingSmsDeliveryStrategy implements SmsDeliveryStrategy {

	private static final Logger log = LoggerFactory.getLogger(LoggingSmsDeliveryStrategy.class);

	@Override
	public Optional<String> sendSms(String fromNumber, String toNumber, String messageText) {
		log.info("Mock SMS message from {} to {}", fromNumber, toNumber);
		log.info("Mock message text:\n" + messageText);
		return Optional.empty();
	}

}
