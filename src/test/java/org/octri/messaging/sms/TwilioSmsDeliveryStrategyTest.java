package org.octri.messaging.sms;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.octri.messaging.exception.InvalidPhoneNumberException;
import org.octri.messaging.exception.UnsuccessfulDeliveryException;
import org.octri.test.messaging.TwilioTestUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

@ExtendWith(MockitoExtension.class)
public class TwilioSmsDeliveryStrategyTest {

	private static final String FROM_NUMBER = "(503) 555-1234";
	private static final String TO_NUMBER = "503.555.6789";
	private static final String MESSAGE_TEXT = "Hi there";

	private static Message queuedMessage;
	private static String queuedMessageJson;
	private static Message failedMessage;
	private static String failedMessageJson;
	private static Message undeliveredMessage;

	@Mock
	private TwilioHelper mockTwilioHelper;

	private TwilioSmsDeliveryStrategy strategy;

	@BeforeAll
	public static void init() throws IOException {
		queuedMessage = TwilioTestUtils.getQueuedMessage();
		queuedMessageJson = TwilioTestUtils.getJsonText("queued.json");
		failedMessage = TwilioTestUtils.getFailedMessage();
		failedMessageJson = TwilioTestUtils.getJsonText("failed.json");
		undeliveredMessage = TwilioTestUtils.getUndeliveredMessage();
	}

	@BeforeEach
	public void setUp() {
		strategy = new TwilioSmsDeliveryStrategy(mockTwilioHelper);
	}

	@Test
	public void testConstructorRequiresTwilioHelper() {
		var thrown = assertThrows(IllegalArgumentException.class, () -> {
			new TwilioSmsDeliveryStrategy(null);
		});
		assertTrue("Twilio helper is required for Twilio SMS delivery strategy.".contains(thrown.getMessage()),
				"Error message should indicate that the Twilio helper is missing.");
	}

	@Test
	public void testConvertsPhoneNumberFormat() throws IOException {
		var expectedFromNumber = new PhoneNumber("+15035551234");
		var expectedToNumber = new PhoneNumber("+15035556789");

		var fromNumberCaptor = ArgumentCaptor.forClass(PhoneNumber.class);
		var toNumberCaptor = ArgumentCaptor.forClass(PhoneNumber.class);

		when(mockTwilioHelper.sendMessage(any(PhoneNumber.class), any(PhoneNumber.class), anyString()))
				.thenReturn(queuedMessage);
		when(mockTwilioHelper.isSuccessResponse(any(Message.class))).thenReturn(true);
		when(mockTwilioHelper.serializeMessageToJson(any(Message.class))).thenReturn(queuedMessageJson);

		strategy.sendSms(FROM_NUMBER, TO_NUMBER, MESSAGE_TEXT);

		verify(mockTwilioHelper).sendMessage(fromNumberCaptor.capture(), toNumberCaptor.capture(), anyString());
		assertEquals(expectedFromNumber, fromNumberCaptor.getValue(), "From number is converted to E.164 format");
		assertEquals(expectedToNumber, toNumberCaptor.getValue(), "To number is converted to E.164 format");
	}

	@Test
	public void testThrowsUnsuccessfulDeliveryOnInvalidPhoneNumber() {
		var tooManyDigits = "1-800-555-1234";
		var deliveryException = assertThrows(UnsuccessfulDeliveryException.class, () -> {
			strategy.sendSms(tooManyDigits, TO_NUMBER, MESSAGE_TEXT);
		}, "It throws an exception when the from number is invalid");
		assertInstanceOf(InvalidPhoneNumberException.class, deliveryException.getCause(),
				"The delivery exception should be caused by an invalid phone number");

		deliveryException = assertThrows(UnsuccessfulDeliveryException.class, () -> {
			strategy.sendSms(FROM_NUMBER, tooManyDigits, MESSAGE_TEXT);
		}, "It throws an exception when the to number is invalid");
		assertInstanceOf(InvalidPhoneNumberException.class, deliveryException.getCause(),
				"The delivery exception should be caused by an invalid phone number");
	}

	@Test
	public void testReturnsStringifiedApiResponse() throws JsonProcessingException {
		when(mockTwilioHelper.sendMessage(any(PhoneNumber.class), any(PhoneNumber.class), anyString()))
				.thenReturn(queuedMessage);
		when(mockTwilioHelper.isSuccessResponse(queuedMessage)).thenReturn(true);
		when(mockTwilioHelper.serializeMessageToJson(queuedMessage)).thenReturn(queuedMessageJson);

		var result = strategy.sendSms(FROM_NUMBER, TO_NUMBER, MESSAGE_TEXT);

		assertTrue(result.isPresent(), "The optional string value should be present");
		var resultJson = result.get();
		assertTrue(resultJson.contains(queuedMessage.getAccountSid()), "API response properties should be present");
	}

	@Test
	public void testThrowsExceptionOnUnsuccessfulDelivery() throws JsonProcessingException {
		when(mockTwilioHelper.sendMessage(any(PhoneNumber.class), any(PhoneNumber.class), anyString()))
				.thenReturn(failedMessage);
		when(mockTwilioHelper.isSuccessResponse(failedMessage)).thenReturn(false);
		when(mockTwilioHelper.serializeMessageToJson(failedMessage)).thenReturn(failedMessageJson);

		assertThrows(UnsuccessfulDeliveryException.class, () -> {
			strategy.sendSms(FROM_NUMBER, TO_NUMBER, MESSAGE_TEXT);
		}, "Delivery is unsuccessful when Twilio helper reports that delivery failed");
	}

	@Test
	public void testExceptionIncludesErrorResponse() throws JsonProcessingException {
		when(mockTwilioHelper.sendMessage(any(PhoneNumber.class), any(PhoneNumber.class), anyString()))
				.thenReturn(failedMessage);
		when(mockTwilioHelper.isSuccessResponse(failedMessage)).thenReturn(false);
		when(mockTwilioHelper.serializeMessageToJson(failedMessage)).thenReturn(failedMessageJson);

		var thrown = assertThrows(UnsuccessfulDeliveryException.class, () -> {
			strategy.sendSms(FROM_NUMBER, TO_NUMBER, MESSAGE_TEXT);
		}, "Delivery is unsuccessful");
		assertTrue(thrown.getErrorResponse().contains(failedMessage.getAccountSid()),
				"API response should be included in error");
	}

	@Test
	public void testOtherExceptionsGetEncapsulated() {
		when(mockTwilioHelper.sendMessage(any(PhoneNumber.class), any(PhoneNumber.class), anyString()))
				.thenThrow(new IllegalStateException("BORK"));

		var thrown = assertThrows(UnsuccessfulDeliveryException.class, () -> {
			strategy.sendSms(FROM_NUMBER, TO_NUMBER, MESSAGE_TEXT);
		}, "Unexpected exceptions are converted to UnsuccessfulDeliveryException");
		assertTrue(thrown.getMessage().contains("Unexpected exception"),
				"The message explains that the error was unexpected");
		assertInstanceOf(IllegalStateException.class, thrown.getCause(), "The original exception is captured");
	}

}
