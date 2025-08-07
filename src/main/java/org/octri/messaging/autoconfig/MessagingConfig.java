package org.octri.messaging.autoconfig;

import java.util.Optional;

import org.octri.messaging.email.EmailDeliveryStrategy;
import org.octri.messaging.email.LoggingEmailDeliveryStrategy;
import org.octri.messaging.email.NoopEmailDeliveryStrategy;
import org.octri.messaging.email.SmtpEmailDeliveryStrategy;
import org.octri.messaging.service.MessageDeliveryService;
import org.octri.messaging.sms.LoggingSmsDeliveryStrategy;
import org.octri.messaging.sms.NoopSmsDeliveryStrategy;
import org.octri.messaging.sms.SmsDeliveryStrategy;
import org.octri.messaging.sms.TwilioHelper;
import org.octri.messaging.sms.TwilioSmsDeliveryStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * Configuration for the messaging library.
 */
@AutoConfiguration
@EnableConfigurationProperties(MessagingProperties.class)
@ConditionalOnProperty(value = "octri.messaging.enabled", havingValue = "true", matchIfMissing = true)
public class MessagingConfig {

	private static final Logger log = LoggerFactory.getLogger(MessagingConfig.class);

	private MessagingProperties messagingProperties;

	/**
	 * Constructor.
	 *
	 * @param messagingProperties
	 *            configuration properties
	 */
	public MessagingConfig(MessagingProperties messagingProperties) {
		this.messagingProperties = messagingProperties;
	}

	/**
	 * Provides the Twilio API helper if Twilio account SID is configured. If the application provides a custom
	 * {@link TwilioHelper} bean, that will be used instead.
	 *
	 * @param messagingProperties
	 *            configuration properties
	 * @return default Twilio helper bean
	 */
	@Bean
	@ConditionalOnMissingBean
	@Conditional(TwilioConfiguredCondition.class)
	public TwilioHelper twilioHelper(MessagingProperties messagingProperties) {
		log.debug("Creating Twilio helper");
		var twilioProperties = messagingProperties.getTwilio();
		return new TwilioHelper(twilioProperties.getAccountSid(), twilioProperties.getAuthToken(),
				twilioProperties.getCallbackUrl());
	}

	/**
	 * Provides the email delivery strategy used by the {@link MessageDeliveryService}. If the application provides a
	 * custom {@link EmailDeliveryStrategy} bean, that will be used instead.
	 *
	 * @param javaMailSender
	 *            optional mail sender bean
	 * @return default email delivery strategy
	 */
	@Bean
	@ConditionalOnMissingBean
	public EmailDeliveryStrategy emailDeliveryStrategy(Optional<JavaMailSender> javaMailSender) {
		var emailDeliveryMethod = messagingProperties.getEmailDeliveryMethod();
		log.debug("Creating email delivery strategy bean for delivery method " + emailDeliveryMethod);

		EmailDeliveryStrategy deliveryStrategy = switch (emailDeliveryMethod) {
			case LOG -> new LoggingEmailDeliveryStrategy();
			case NOOP -> new NoopEmailDeliveryStrategy();
			case SMTP -> new SmtpEmailDeliveryStrategy(javaMailSender.get());
			default -> throw new IllegalArgumentException("Invalid email delivery method " + emailDeliveryMethod);
		};

		log.debug("Email delivery strategy: " + deliveryStrategy.getClass().getName());
		return deliveryStrategy;
	}

	/**
	 * Provides the SMS delivery strategy used by the {@link MessageDeliveryService}. If the application provides a
	 * custom {@link SmsDeliveryStrategy} bean, that will be used instead.
	 *
	 * @param twilioHelper
	 *            optional Twilio helper bean
	 * @return default SMS delivery strategy
	 */
	@Bean
	@ConditionalOnMissingBean
	public SmsDeliveryStrategy smsDeliveryStrategy(Optional<TwilioHelper> twilioHelper) {
		var smsDeliveryMethod = messagingProperties.getSmsDeliveryMethod();

		log.debug("Creating SMS delivery strategy bean for delivery method " + smsDeliveryMethod);

		SmsDeliveryStrategy deliveryStrategy = switch (smsDeliveryMethod) {
			case LOG -> new LoggingSmsDeliveryStrategy();
			case NOOP -> new NoopSmsDeliveryStrategy();
			case TWILIO -> new TwilioSmsDeliveryStrategy(twilioHelper.get());
			default -> throw new IllegalArgumentException("Invalid SMS delivery method" + smsDeliveryMethod);
		};

		log.debug("SMS delivery strategy: " + deliveryStrategy.getClass().getName());
		return deliveryStrategy;
	}

	/**
	 * Provides the {@link MessageDeliveryService}.
	 *
	 * @param emailStrategy
	 *            email delivery strategy
	 * @param smsStrategy
	 *            SMS delivery strategy
	 * @return the message delivery service
	 */
	@Bean
	public MessageDeliveryService messageDeliveryService(EmailDeliveryStrategy emailStrategy,
			SmsDeliveryStrategy smsStrategy) {
		log.debug("Creating message delivery service.");
		if (emailStrategy == null || smsStrategy == null) {
			log.error("Attempted to create the message delivery service, but a required delivery strategy was null.");
			log.error("Email strategy: " + emailStrategy + " SMS strategy: " + smsStrategy);
			throw new IllegalStateException("Cannot create message delivery service due to missing delivery strategy.");
		}

		return new MessageDeliveryService(emailStrategy, smsStrategy);
	}

}
