package org.octri.messaging.email;

import java.util.Optional;

import org.octri.messaging.autoconfig.EmailProperties;
import org.octri.messaging.exception.UnsuccessfulDeliveryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import io.micrometer.common.util.StringUtils;

/**
 * Mock email delivery strategy for development environments that logs messages
 * to the console for inspection.
 */
public class LoggingEmailDeliveryStrategy implements EmailDeliveryStrategy {

	private static final Logger log = LoggerFactory.getLogger(LoggingEmailDeliveryStrategy.class);

	private final EmailProperties emailProperties;

	/**
	 * Constructor.
	 *
	 * @param emailProperties
	 *            email configuration properties
	 */
	public LoggingEmailDeliveryStrategy(EmailProperties emailProperties) {
		Assert.notNull(emailProperties, "Email configuration properties are required for the logging delivery strategy."
				+ " Check the octri.messaging.email configuration.");
		this.emailProperties = emailProperties;
	}

	/**
	 * Logs the message to the console for inspection.
	 */
	@Override
	public Optional<String> sendEmail(String fromEmail, String toEmail, String messageSubject, String messageText) {
		var prefixedSubject = EmailUtils.addPrefixToSubject(messageSubject, emailProperties.getSubjectPrefix());
		log.info("Mock email from {} to {}", fromEmail, toEmail);
		log.info("Mock message subject: " + prefixedSubject);
		log.info("Mock message text:\n" + messageText);
		return Optional.empty();
	}

	/**
	 * Logs the message for the console for inspection, using the default sender address.
	 */
	@Override
	public Optional<String> sendEmail(String toEmail, String messageSubject, String messageText) {
		if (StringUtils.isBlank(emailProperties.getDefaultSenderAddress())) {
			throw new UnsuccessfulDeliveryException(
					"The default sender address is required to send email without a from address.");
		}
		return sendEmail(emailProperties.getDefaultSenderAddress(), toEmail, messageSubject, messageText);
	}

}
