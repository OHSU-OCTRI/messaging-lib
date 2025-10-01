package org.octri.messaging.autoconfig;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for the messaging library.
 */
@ConfigurationProperties(prefix = "octri.messaging")
public class MessagingProperties {

	/**
	 * Valid email delivery methods.
	 */
	public enum EmailDeliveryMethod {
		/**
		 * Log messages that would be sent, but do not actually deliver them.
		 */
		LOG,

		/**
		 * Disable email delivery, and discard any messages.
		 */
		NOOP,

		/**
		 * Deliver email messages via SMTP.
		 */
		SMTP
	}

	/**
	 * Valid SMS delivery methods.
	 */
	public enum SmsDeliveryMethod {
		/**
		 * Log messages that would be sent, but do not actually deliver them.
		 */
		LOG,

		/**
		 * Disable SMS delivery, and discard any messages.
		 */
		NOOP,

		/**
		 * Deliver SMS messages using the Twilio API.
		 */
		TWILIO
	}

	/**
	 * Whether to enable messaging services. True by default.
	 */
	private boolean enabled = true;

	/**
	 * Properties to configure the email delivery strategy.
	 */
	private EmailProperties email;

	/**
	 * How email messages should be delivered. Defaults to sending messages to the log.
	 */
	private EmailDeliveryMethod emailDeliveryMethod = EmailDeliveryMethod.LOG;

	/**
	 * How SMS messages should be delivered. Defaults to sending messages to the log.
	 */
	private SmsDeliveryMethod smsDeliveryMethod = SmsDeliveryMethod.LOG;

	/**
	 * Properties to configure the Twilio SMS delivery strategy.
	 */
	private TwilioProperties twilio;

	/**
	 * Gets whether messaging is enabled.
	 *
	 * @return whether messaging is enabled
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * Sets whether to enable messaging.
	 *
	 * @param enabled
	 *            true to enable messaging, false if not
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * Gets the email configuration properties.
	 * 
	 * @return email configuration
	 */
	public EmailProperties getEmail() {
		return email;
	}

	/**
	 * Sets the email configuration properties.
	 * 
	 * @param email
	 *            email configuration
	 */
	public void setEmail(EmailProperties email) {
		this.email = email;
	}

	/**
	 * Gets the configured email delivery method.
	 *
	 * @return the configured email delivery method
	 */
	public EmailDeliveryMethod getEmailDeliveryMethod() {
		return emailDeliveryMethod;
	}

	/**
	 * Sets the email delivery method to use.
	 *
	 * @param emailDeliveryMethod
	 *            the email delivery method to use
	 */
	public void setEmailDeliveryMethod(EmailDeliveryMethod emailDeliveryMethod) {
		this.emailDeliveryMethod = emailDeliveryMethod;
	}

	/**
	 * Gets the configured SMS delivery method.
	 *
	 * @return the configured SMS delivery method
	 */
	public SmsDeliveryMethod getSmsDeliveryMethod() {
		return smsDeliveryMethod;
	}

	/**
	 * Sets the SMS delivery method to use.
	 *
	 * @param smsDeliveryMethod
	 *            the SMS delivery method to use
	 */
	public void setSmsDeliveryMethod(SmsDeliveryMethod smsDeliveryMethod) {
		this.smsDeliveryMethod = smsDeliveryMethod;
	}

	/**
	 * Gets the Twilio API configuration properties.
	 *
	 * @return Twilio API configuration
	 */
	public TwilioProperties getTwilio() {
		return twilio;
	}

	/**
	 * Sets the Twilio API configuration properties.
	 *
	 * @param twilio
	 *            Twilio API configuration
	 */
	public void setTwilio(TwilioProperties twilio) {
		this.twilio = twilio;
	}

}
