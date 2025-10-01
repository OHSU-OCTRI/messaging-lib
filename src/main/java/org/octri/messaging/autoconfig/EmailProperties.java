package org.octri.messaging.autoconfig;

/**
 * Properties used to configure the email delivery strategy.
 */
public class EmailProperties {

	/**
	 * Default sender email address.
	 */
	private String defaultSenderAddress;

	/**
	 * Email subject prefix.
	 */
	private String subjectPrefix;

	/**
	 * Gets the email address used if a sender address is not provided.
	 *
	 * @return the default sender address
	 */
	public String getDefaultSenderAddress() {
		return defaultSenderAddress;
	}

	/**
	 * Sets the email address used if a sender address is not provided.
	 *
	 * @param defaultSenderAddress
	 *            the address to use
	 */
	public void setDefaultSenderAddress(String defaultSenderAddress) {
		this.defaultSenderAddress = defaultSenderAddress;
	}

	/**
	 * Gets the prefix added to email subject lines. May be blank.
	 *
	 * @return the subject prefix
	 */
	public String getSubjectPrefix() {
		return subjectPrefix;
	}

	/**
	 * Sets the prefix added to email subject lines. May be blank.
	 *
	 * @param subjectPrefix
	 *            the prefix to use
	 */
	public void setSubjectPrefix(String subjectPrefix) {
		this.subjectPrefix = subjectPrefix;
	}

}
