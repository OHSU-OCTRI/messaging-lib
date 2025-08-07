package org.octri.messaging.sms;

import java.util.Optional;

/**
 * Do-nothing SMS delivery strategy for environments that want to disable SMS
 * support entirely.
 */
public class NoopSmsDeliveryStrategy implements SmsDeliveryStrategy {

	@Override
	public Optional<String> sendSms(String fromNumber, String toNumber, String messageText) {
		return Optional.empty();
	}

}
