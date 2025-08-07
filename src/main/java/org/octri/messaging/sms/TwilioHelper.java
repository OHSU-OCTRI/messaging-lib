package org.octri.messaging.sms;

import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.octri.messaging.exception.InvalidPhoneNumberException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

/**
 * Helper methods for working with the Twilio SMS API.
 */
public class TwilioHelper {

	private static final Logger log = LoggerFactory.getLogger(TwilioHelper.class);

	private final Set<Message.Status> failedStatuses = Set.of(Message.Status.FAILED, Message.Status.UNDELIVERED);
	private final ObjectMapper objectMapper;
	private final String accountSid;
	private final String authToken;
	private final String callbackUrl;

	/**
	 * Constructor.
	 *
	 * @param accountSid
	 *            Twilio account ID. Required.
	 * @param authToken
	 *            Twilio account auth token. Required.
	 * @param callbackUrl
	 *            SMS message status callback URL. If provided, this will be
	 *            provided to Twilio for status updates.
	 */
	public TwilioHelper(String accountSid, String authToken, String callbackUrl) {
		Assert.hasLength(accountSid, "Twilio account SID is required.");
		Assert.hasLength(authToken, "Twilio auth token is required.");

		this.accountSid = accountSid;
		this.authToken = authToken;
		this.callbackUrl = callbackUrl;

		this.objectMapper = new ObjectMapper();
		this.objectMapper.registerModule(new JavaTimeModule());

		var customModule = new SimpleModule();
		customModule.addDeserializer(PhoneNumber.class, new TwilioPhoneNumberDeserializer());
		this.objectMapper.registerModule(customModule);

		Twilio.init(this.accountSid, this.authToken);
	}

	/**
	 * Parses a Twilio message serialized to JSON.
	 *
	 * @param jsonString
	 *            JSON text
	 * @return a Twilio message
	 */
	public Message loadMessageFromString(String jsonString) {
		return Message.fromJson(jsonString, objectMapper);
	}

	/**
	 * Serializes the given Twilio message to JSON.
	 *
	 * @param message
	 *            a Twilio message
	 * @return JSON text
	 * @throws JsonProcessingException
	 *             if the object mapper is unable to serialize the message
	 */
	public String serializeMessageToJson(Message message) throws JsonProcessingException {
		return objectMapper.writeValueAsString(message);
	}

	/**
	 * Sends an SMS message via the Twilio API.
	 *
	 * @param fromNumber
	 *            sender phone number
	 * @param toNumber
	 *            recipient phone number
	 * @param messageText
	 *            message body text
	 * @return the Twilio message API response
	 */
	public Message sendMessage(PhoneNumber fromNumber, PhoneNumber toNumber, String messageText) {
		var creator = Message.creator(toNumber, fromNumber, messageText);

		if (StringUtils.isNotEmpty(callbackUrl)) {
			creator.setStatusCallback(callbackUrl);
		}

		return creator.create();
	}

	/**
	 * Sends an SMS message via the Twilio API. Phone number strings are automatically converted to the E.164 format
	 * expected by Twilio. An {@link InvalidPhoneNumberException} is thrown if a phone number cannot be converted.
	 *
	 * @param fromNumber
	 *            sender phone number
	 * @param toNumber
	 *            recipient phone number
	 * @param messageText
	 *            message body text
	 * @return the Twilio message API response
	 */
	public Message sendMessage(String fromNumber, String toNumber, String messageText) {
		var fromNumberE164 = SmsUtils.toE164PhoneNumber(fromNumber);
		var toNumberE164 = SmsUtils.toE164PhoneNumber(toNumber);
		return sendMessage(new PhoneNumber(fromNumberE164), new PhoneNumber(toNumberE164), messageText);
	}

	/**
	 * Gets the current status of the SMS message with the given SID via the Twilio API.
	 *
	 * @param messageSid
	 *            message ID string
	 * @return the Twilio message API response
	 */
	public Message fetchMessage(String messageSid) {
		return Message.fetcher(messageSid).fetch();
	}

	/**
	 * Reports whether the given Twilio {@link Message} is in one of the success states. Messages are considered
	 * successful as long as they have not entered one of the failure states (failed or undelivered).
	 *
	 * @param message
	 *            a Twilio message
	 * @return false if the message's status is "failed" or "undelivered", true
	 *         otherwise
	 */
	public boolean isSuccessResponse(Message message) {
		if (failedStatuses.contains(message.getStatus())) {
			return false;
		}

		return true;
	}

}
