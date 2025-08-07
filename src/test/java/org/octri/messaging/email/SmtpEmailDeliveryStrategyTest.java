package org.octri.messaging.email;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.octri.messaging.exception.UnsuccessfulDeliveryException;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@ExtendWith(MockitoExtension.class)
public class SmtpEmailDeliveryStrategyTest {

	private static final String EXPECTED_SENDER = "sender@example.com";
	private static final String EXPECTED_RECIPIENT = "recipient@example.com";
	private static final String EXPECTED_SUBJECT = "Subject";
	private static final String EXPECTED_BODY = "Body";

	@Mock
	JavaMailSender mockMailSender;

	@Test
	public void testRequiresJavaMailSender() {
		assertThrows(IllegalArgumentException.class, () -> {
			new SmtpEmailDeliveryStrategy(null);
		});
	}

	@Test
	public void testMessageConstruction() {
		var strategy = new SmtpEmailDeliveryStrategy(mockMailSender);
		var argument = ArgumentCaptor.forClass(SimpleMailMessage.class);

		var result = strategy.sendEmail(EXPECTED_SENDER, EXPECTED_RECIPIENT, EXPECTED_SUBJECT, EXPECTED_BODY);

		verify(mockMailSender).send(argument.capture());

		var message = argument.getValue();
		assertEquals(EXPECTED_SENDER, message.getFrom());
		assertEquals(1, message.getTo().length);
		assertEquals(EXPECTED_RECIPIENT, message.getTo()[0]);
		assertEquals(EXPECTED_SUBJECT, message.getSubject());
		assertEquals(EXPECTED_BODY, message.getText());

		assertFalse(result.isPresent(), "No delivery details should be returned.");
	}

	@Test
	public void testExceptionConversion() {
		var expectedMessage = "Fake auth exception";
		var strategy = new SmtpEmailDeliveryStrategy(mockMailSender);
		doThrow(new MailAuthenticationException(expectedMessage)).when(mockMailSender)
				.send(any(SimpleMailMessage.class));

		var thrownException = assertThrows(UnsuccessfulDeliveryException.class, () -> {
			strategy.sendEmail(EXPECTED_SENDER, EXPECTED_RECIPIENT, EXPECTED_SUBJECT, EXPECTED_BODY);
		});

		var cause = thrownException.getCause();
		assertEquals(cause.getMessage(), expectedMessage);
	}

}
