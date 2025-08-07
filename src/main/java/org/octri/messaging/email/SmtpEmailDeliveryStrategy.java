package org.octri.messaging.email;

import java.util.Optional;

import org.octri.messaging.exception.UnsuccessfulDeliveryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.util.Assert;

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

	private JavaMailSender sender;

	/**
	 * Constructor.
	 * 
	 * @param sender
	 *            java mail sender
	 */
	public SmtpEmailDeliveryStrategy(JavaMailSender sender) {
		Assert.notNull(sender, "A JavaMailSender bean is required for the SMTP delivery strategy."
				+ " Check the spring.mail configuration");
		this.sender = sender;
	}

	@Override
	public Optional<String> sendEmail(String fromEmail, String toEmail, String messageSubject, String messageText) {
		log.debug("Sending SMTP email from {} to {}", fromEmail, toEmail);
		log.debug("Message subject: " + messageSubject);
		log.debug("Message text:\n" + messageText);

		var message = new SimpleMailMessage();
		message.setFrom(fromEmail);
		message.setTo(toEmail);
		message.setSubject(messageSubject);
		message.setText(messageText);

		try {
			sender.send(message);
		} catch (MailException ex) {
			throw new UnsuccessfulDeliveryException("SMTP delivery failed", ex);
		}

		return Optional.empty();
	}
}
