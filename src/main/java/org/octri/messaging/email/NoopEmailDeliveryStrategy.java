package org.octri.messaging.email;

import java.util.Optional;

/**
 * Do-nothing email delivery strategy for environments that want to disable
 * email support entirely.
 */
public class NoopEmailDeliveryStrategy implements EmailDeliveryStrategy {

	@Override
	public Optional<String> sendEmail(String fromEmail, String toEmail, String messageSubject, String messageText) {
		return Optional.empty();
	}

	@Override
	public Optional<String> sendEmail(String toEmail, String messageSubject, String messageText) {
		return Optional.empty();
	}

}
