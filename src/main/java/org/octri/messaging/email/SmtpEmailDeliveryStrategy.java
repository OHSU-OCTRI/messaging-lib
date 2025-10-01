package org.octri.messaging.email;

import java.util.Optional;

import org.octri.messaging.autoconfig.EmailProperties;
import org.octri.messaging.exception.UnsuccessfulDeliveryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.util.Assert;

import io.micrometer.common.util.StringUtils;

/**
 * Email delivery strategy that uses an SMTP server to send messages.
 *
 * This depends on Spring Boot's email support, so your application should add a dependency on
 * <code>spring-boot-starter-mail</code> and configure the related <code>spring.mail</code> properties in
 * <code>application.properties</code>.
 *
 * @see <a href=
 *      "https://docs.spring.io/spring-boot/reference/io/email.html">https://docs.spring.io/spring-boot/reference/io/email.html</a>
 * @see <a href=
 *      "https://docs.spring.io/spring-boot/appendix/application-properties/index.html#appendix.application-properties.mail">https://docs.spring.io/spring-boot/appendix/application-properties/index.html#appendix.application-properties.mail</a>
 */
public class SmtpEmailDeliveryStrategy implements EmailDeliveryStrategy {

	private static final Logger log = LoggerFactory.getLogger(SmtpEmailDeliveryStrategy.class);

	private final JavaMailSender sender;
	private final EmailProperties emailProperties;

	/**
	 * Constructor.
	 *
	 * @param sender
	 *            java mail sender
	 * @param emailProperties
	 *            email configuration properties
	 */
	public SmtpEmailDeliveryStrategy(JavaMailSender sender, EmailProperties emailProperties) {
		Assert.notNull(sender, "A JavaMailSender bean is required for the SMTP delivery strategy."
				+ " Check the spring.mail configuration");
		Assert.notNull(emailProperties, "Email configuration properties are required for the SMTP delivery strategy."
				+ " Check the octri.messaging.email configuration.");
		this.sender = sender;
		this.emailProperties = emailProperties;
	}

	@Override
	public Optional<String> sendEmail(String fromEmail, String toEmail, String messageSubject, String messageText) {
		var prefixedSubject = EmailUtils.addPrefixToSubject(messageSubject, emailProperties.getSubjectPrefix());
		log.debug("Sending SMTP email from {} to {}", fromEmail, toEmail);
		log.debug("Message subject: " + prefixedSubject);
		log.debug("Message text:\n" + messageText);

		var message = new SimpleMailMessage();
		message.setFrom(fromEmail);
		message.setTo(toEmail);
		message.setSubject(prefixedSubject);
		message.setText(messageText);

		try {
			sender.send(message);
		} catch (MailException ex) {
			throw new UnsuccessfulDeliveryException("SMTP delivery failed", ex);
		}

		return Optional.empty();
	}

	@Override
	public Optional<String> sendEmail(String toEmail, String messageSubject, String messageText) {
		if (StringUtils.isBlank(emailProperties.getDefaultSenderAddress())) {
			throw new UnsuccessfulDeliveryException(
					"The default sender address is required to send email without a from address.");
		}
		return sendEmail(emailProperties.getDefaultSenderAddress(), toEmail, messageSubject, messageText);
	}

}
