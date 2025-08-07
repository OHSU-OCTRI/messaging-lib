package org.octri.messaging.autoconfig;

/**
 * Properties used to configure the Twilio SMS delivery strategy. These are available on the Twilio console.
 *
 * To deliver messages, you will also need to purchase a Twilio SMS number.
 *
 * @see <a href="https://twilio.com/console">https://twilio.com/console</a>
 * @see <a href=
 *      "https://www.twilio.com/console/phone-numbers/search">https://www.twilio.com/console/phone-numbers/search</a>
 */
public class TwilioProperties {

	/**
	 * Twilio account SID from the Twilio console.
	 */
	private String accountSid;

	/**
	 * Twilio auth token from the Twilio console.
	 */
	private String authToken;

	/**
	 * Optional Twilio message status callback URL. If configured, this URL is provided to Twilio API when sending
	 * messages, allowing Twilio to report message status changes to your application. This URL must be public.
	 *
	 * @see <a href=
	 *      "https://www.twilio.com/docs/messaging/guides/track-outbound-message-status">https://www.twilio.com/docs/messaging/guides/track-outbound-message-status</a>
	 */
	private String callbackUrl;

	/**
	 * Gets the configured Twilio account SID.
	 *
	 * @return the account SID
	 */
	public String getAccountSid() {
		return accountSid;
	}

	/**
	 * Sets the Twilio account SID.
	 *
	 * @param accountSid
	 *            account SID string
	 */
	public void setAccountSid(String accountSid) {
		this.accountSid = accountSid;
	}

	/**
	 * Gets the configured Twilio auth token.
	 *
	 * @return the Twilio auth token
	 */
	public String getAuthToken() {
		return authToken;
	}

	/**
	 * Sets the Twilio auth token.
	 *
	 * @param authToken
	 *            auth token string
	 */
	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}

	/**
	 * Gets the configured Twilio message status callback URL.
	 *
	 * @return the message status callback URL
	 */
	public String getCallbackUrl() {
		return callbackUrl;
	}

	/**
	 * Sets the Twilio message status callback URL.
	 *
	 * @param callbackUrl
	 *            the callback URL
	 */
	public void setCallbackUrl(String callbackUrl) {
		this.callbackUrl = callbackUrl;
	}

}
