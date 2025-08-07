package org.octri.messaging.service;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.octri.messaging.email.EmailDeliveryStrategy;
import org.octri.messaging.sms.SmsDeliveryStrategy;

@ExtendWith(MockitoExtension.class)
public class MessageDeliveryServiceTest {

	@Mock
	EmailDeliveryStrategy mockEmailStrategy;

	@Mock
	SmsDeliveryStrategy mockSmsStrategy;

	@Test
	public void testDelegatesEmailDeliveryToStrategy() {
		var senderAddress = "sender@example.com";
		var recipientAddress = "recipient@example.com";
		var messageSubject = "Subject";
		var messageBody = "Body";

		var service = new MessageDeliveryService(mockEmailStrategy, mockSmsStrategy);
		service.sendEmail(senderAddress, recipientAddress, messageSubject, messageBody);
		verify(mockEmailStrategy).sendEmail(senderAddress, recipientAddress, messageSubject, messageBody);
	}

	@Test
	public void testDelegatesSmsDeliveryToStrategy() {
		var senderNumber = "+15551234567";
		var recipientNumber = "+15554567890";
		var messageText = "Text";

		var service = new MessageDeliveryService(mockEmailStrategy, mockSmsStrategy);
		service.sendSms(senderNumber, recipientNumber, messageText);
		verify(mockSmsStrategy).sendSms(senderNumber, recipientNumber, messageText);
	}
}
