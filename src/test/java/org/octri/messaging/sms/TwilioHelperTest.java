package org.octri.messaging.sms;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.description;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.octri.test.messaging.TwilioTestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.api.v2010.account.MessageCreator;
import com.twilio.rest.api.v2010.account.MessageFetcher;
import com.twilio.type.PhoneNumber;

public class TwilioHelperTest {

	public static final Logger log = LoggerFactory.getLogger(TwilioHelperTest.class);

	private static final String MOCK_SID = "mockSid";
	private static final String MOCK_TOKEN = "mockToken";
	private static final String MOCK_CALLBACK_URL = "http://localhost:8080/open/twilio/callback";
	private static final String FROM_NUMBER = "(503) 555-1234";
	private static final String TO_NUMBER = "503.555.6789";
	private static final String MESSAGE_TEXT = "Hi there";
	private static final String MESSAGE_SID = "SM46b08410f6fdd3e292cac5b5d87121d7";

	private static Message queuedMessage;
	private static Message failedMessage;
	private static Message undeliveredMessage;
	private static Message deliveredMessage;

	private MockedStatic<Twilio> mockTwilio;
	private MockedStatic<Message> mockMessage;

	private TwilioHelper twilioHelper;

	@BeforeAll
	public static void init() throws IOException {
		queuedMessage = TwilioTestUtils.getQueuedMessage();
		failedMessage = TwilioTestUtils.getFailedMessage();
		undeliveredMessage = TwilioTestUtils.getUndeliveredMessage();
		deliveredMessage = TwilioTestUtils.getDeliveredMessage();
	}

	@BeforeEach
	public void setup() {
		mockTwilio = Mockito.mockStatic(Twilio.class);
		mockMessage = Mockito.mockStatic(Message.class);

		twilioHelper = new TwilioHelper(MOCK_SID, MOCK_TOKEN, MOCK_CALLBACK_URL);
	}

	@AfterEach
	public void tearDown() {
		mockTwilio.close();
		mockMessage.close();
	}

	@Test
	public void testConstructorInitializesTwilioApi() {
		// See https://stackoverflow.com/a/62860455
		mockTwilio.verify(() -> Twilio.init(eq(MOCK_SID), eq(MOCK_TOKEN)));
	}

	@Test
	public void testConstructorRequiresCredentials() {
		var thrown = assertThrows(IllegalArgumentException.class, () -> {
			new TwilioHelper(null, MOCK_TOKEN, null);
		});
		assertTrue("Twilio account SID is required.".contains(thrown.getMessage()),
				"Error message should indicate that the account SID is missing.");

		thrown = assertThrows(IllegalArgumentException.class, () -> {
			new TwilioHelper(MOCK_SID, null, null);
		});
		assertTrue("Twilio auth token is required.".contains(thrown.getMessage()),
				"Error message should indicate that the auth token is missing.");
	}

	@Test
	public void testSerialization() throws Exception {
		mockMessage.when(() -> Message.fromJson(anyString(), any(ObjectMapper.class))).thenCallRealMethod();
		var json = twilioHelper.serializeMessageToJson(queuedMessage);
		var msg = twilioHelper.loadMessageFromString(json);
		assertEquals(queuedMessage.getAccountSid(), msg.getAccountSid(),
				"Message serialization and deserialization should work (account SID)");
		assertEquals(queuedMessage.getSid(), msg.getSid(),
				"Message serialization and deserialization should work (message SID)");
		assertEquals(queuedMessage.getFrom(), msg.getFrom(),
				"Message serialization and deserialization should work (from number)");
		assertEquals(queuedMessage.getTo(), msg.getTo(),
				"Message serialization and deserialization should work (to number)");
		assertEquals(queuedMessage.getStatus(), msg.getStatus(),
				"Message serialization and deserialization should work (status)");
	}

	@Test
	public void testSendMessageConvertsPhoneNumbers() {
		var expectedFromNumber = new PhoneNumber("+15035551234");
		var expectedToNumber = new PhoneNumber("+15035556789");

		var fromNumberCaptor = ArgumentCaptor.forClass(PhoneNumber.class);
		var toNumberCaptor = ArgumentCaptor.forClass(PhoneNumber.class);
		var mockMessageCreator = Mockito.mock(MessageCreator.class);

		mockMessage.when(() -> Message.creator(toNumberCaptor.capture(), fromNumberCaptor.capture(), anyString()))
				.thenReturn(mockMessageCreator);
		doReturn(queuedMessage).when(mockMessageCreator).create();

		twilioHelper.sendMessage(FROM_NUMBER, TO_NUMBER, MESSAGE_TEXT);

		assertEquals(expectedFromNumber, fromNumberCaptor.getValue(), "From number is converted to E.164 format");
		assertEquals(expectedToNumber, toNumberCaptor.getValue(), "To number is converted to E.164 format");
	}

	@Test
	public void testSendMessageSetsCallbackUrlWhenAvailable() {
		var mockMessageCreator = Mockito.mock(MessageCreator.class);

		mockMessage.when(() -> Message.creator(any(PhoneNumber.class), any(PhoneNumber.class), anyString()))
				.thenReturn(mockMessageCreator);
		doReturn(queuedMessage).when(mockMessageCreator).create();

		twilioHelper.sendMessage(FROM_NUMBER, TO_NUMBER, MESSAGE_TEXT);

		verify(mockMessageCreator,
				description("setStatusCallback should be called when the helper's callback URL is set"))
				.setStatusCallback(MOCK_CALLBACK_URL);
	}

	@Test
	public void testSendMessageDoesNotSetCallbackUrlIfUnavailable() {
		var noCallbackHelper = new TwilioHelper(MOCK_SID, MOCK_TOKEN, null);
		var mockMessageCreator = Mockito.mock(MessageCreator.class);
		mockMessage.when(() -> Message.creator(any(PhoneNumber.class), any(PhoneNumber.class), anyString()))
				.thenReturn(mockMessageCreator);
		doReturn(queuedMessage).when(mockMessageCreator).create();

		noCallbackHelper.sendMessage(FROM_NUMBER, TO_NUMBER, MESSAGE_TEXT);

		verify(mockMessageCreator,
				times(0).description("setStatusCallback should not be called when the helper's callback URL is null"))
				.setStatusCallback(anyString());
	}

	@Test
	public void testSendMessageDoesNotSetCallbackUrlIfBlank() {
		var noCallbackHelper = new TwilioHelper(MOCK_SID, MOCK_TOKEN, "");
		var mockMessageCreator = Mockito.mock(MessageCreator.class);
		mockMessage.when(() -> Message.creator(any(PhoneNumber.class), any(PhoneNumber.class), anyString()))
				.thenReturn(mockMessageCreator);
		doReturn(queuedMessage).when(mockMessageCreator).create();

		noCallbackHelper.sendMessage(FROM_NUMBER, TO_NUMBER, MESSAGE_TEXT);

		verify(mockMessageCreator,
				times(0).description("setStatusCallback should not be called when the helper's callback URL is blank"))
				.setStatusCallback(anyString());
	}

	@Test
	public void testFetchMessage() {
		var messageSidCaptor = ArgumentCaptor.forClass(String.class);
		var mockMessageFetcher = Mockito.mock(MessageFetcher.class);
		mockMessage.when(() -> Message.fetcher(messageSidCaptor.capture())).thenReturn(mockMessageFetcher);
		doReturn(deliveredMessage).when(mockMessageFetcher).fetch();

		var messageStatus = twilioHelper.fetchMessage(MESSAGE_SID);

		assertEquals(MESSAGE_SID, messageSidCaptor.getValue(), "It should create a fetcher for the expected SID");
		verify(mockMessageFetcher, description("The message status should be fetched")).fetch();
		assertEquals(deliveredMessage, messageStatus, "The Twilio API response should be returned");
	}

	@Test
	public void testIsSuccessResponse() {
		assertTrue(twilioHelper.isSuccessResponse(deliveredMessage),
				"Delivered status should be considered successful");
		assertTrue(twilioHelper.isSuccessResponse(queuedMessage), "Queued status should be considered successful");
		assertFalse(twilioHelper.isSuccessResponse(failedMessage), "Failed status should not be considered successful");
		assertFalse(twilioHelper.isSuccessResponse(undeliveredMessage),
				"Undelivered status should not be considered successful");
	}

}
