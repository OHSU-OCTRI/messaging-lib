package org.octri.messaging.sms;

import java.util.Optional;

import org.octri.messaging.exception.InvalidPhoneNumberException;
import org.octri.messaging.exception.UnsuccessfulDeliveryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

/**
 * SMS delivery strategy that uses the Twilio API to send messages.
 */
public class TwilioSmsDeliveryStrategy implements SmsDeliveryStrategy {

	private static final Logger log = LoggerFactory.getLogger(TwilioSmsDeliveryStrategy.class);

	private final TwilioHelper twilioHelper;

	/**
	 * Constructor.
	 * 
	 * @param twilioHelper
	 *            Twilio API helper
	 */
	public TwilioSmsDeliveryStrategy(TwilioHelper twilioHelper) {
		Assert.notNull(twilioHelper, "Twilio helper is required for Twilio SMS delivery strategy.");
		this.twilioHelper = twilioHelper;
	}

	@Override
	public Optional<String> sendSms(String fromNumber, String toNumber, String messageText) {
		try {
			var fromNumberE164 = SmsUtils.toE164PhoneNumber(fromNumber);
			var toNumberE164 = SmsUtils.toE164PhoneNumber(toNumber);
			var message = twilioHelper.sendMessage(new PhoneNumber(fromNumberE164), new PhoneNumber(toNumberE164),
					messageText);
			return convertApiResponse(message);
		} catch (UnsuccessfulDeliveryException ude) {
			throw ude;
		} catch (InvalidPhoneNumberException ipne) {
			throw new UnsuccessfulDeliveryException("Failed to convert phone number to E.164 format.", ipne);
		} catch (Exception e) {
			var errorMsg = "Unexpected exception delivering SMS: " + e.getClass().getCanonicalName();
			log.error(errorMsg, e);
			throw new UnsuccessfulDeliveryException(errorMsg, e);
		}
	}

	private Optional<String> convertApiResponse(Message message) {
		var deliverySuccessful = twilioHelper.isSuccessResponse(message);
		var apiJson = "";

		try {
			apiJson = twilioHelper.serializeMessageToJson(message);
		} catch (JsonProcessingException jpe) {
			log.error("Could not convert Twilio response to JSON. Using toString instead.");
			apiJson = message.toString();
		}

		if (!deliverySuccessful) {
			throw new UnsuccessfulDeliveryException("Twilio delivery failed.", null, apiJson);
		}

		return Optional.of(apiJson);
	}

}
