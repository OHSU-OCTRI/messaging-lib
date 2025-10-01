package org.octri.messaging.email;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.octri.messaging.autoconfig.EmailProperties;
import org.octri.messaging.exception.UnsuccessfulDeliveryException;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@ExtendWith(MockitoExtension.class)
public class SmtpEmailDeliveryStrategyTest {

	private static final String DEFAULT_SENDER = "default@example.com";
	private static final String EXPECTED_SENDER = "sender@example.com";
	private static final String EXPECTED_RECIPIENT = "recipient@example.com";
	private static final String EXPECTED_SUBJECT = "Subject";
	private static final String EXPECTED_BODY = "Body";

	@Mock
	JavaMailSender mockMailSender;

	EmailProperties emailProperties;

	@BeforeEach
	public void setup() {
		emailProperties = new EmailProperties();
		emailProperties.setDefaultSenderAddress(DEFAULT_SENDER);
	}

	@Test
	public void testRequiresJavaMailSender() {
		assertThrows(IllegalArgumentException.class, () -> {
			new SmtpEmailDeliveryStrategy(null, emailProperties);
		}, "The JavaMailSender should be required.");
	}

	@Test
	public void testRequiresEmailProperties() {
		assertThrows(IllegalArgumentException.class, () -> {
			new SmtpEmailDeliveryStrategy(mockMailSender, null);
		}, "The email configuration properties should be required.");
	}

	@Test
	public void testMessageConstruction() {
		var strategy = new SmtpEmailDeliveryStrategy(mockMailSender, emailProperties);
		var argument = ArgumentCaptor.forClass(SimpleMailMessage.class);

		var result = strategy.sendEmail(EXPECTED_SENDER, EXPECTED_RECIPIENT, EXPECTED_SUBJECT, EXPECTED_BODY);

		verify(mockMailSender).send(argument.capture());

		var message = argument.getValue();
		assertEquals(EXPECTED_SENDER, message.getFrom(), "The message should have the expected sender.");
		assertEquals(1, message.getTo().length, "The message should have one recipient.");
		assertEquals(EXPECTED_RECIPIENT, message.getTo()[0], "The message should have the expected recipient.");
		assertEquals(EXPECTED_SUBJECT, message.getSubject(), "The message should have the expected subject.");
		assertEquals(EXPECTED_BODY, message.getText(), "The message should have the expected text.");

		assertFalse(result.isPresent(), "No delivery details should be returned.");
	}

	@Test
	public void testDefaultSender() {
		var strategy = new SmtpEmailDeliveryStrategy(mockMailSender, emailProperties);
		var argument = ArgumentCaptor.forClass(SimpleMailMessage.class);

		strategy.sendEmail(EXPECTED_RECIPIENT, EXPECTED_SUBJECT, EXPECTED_BODY);
		verify(mockMailSender).send(argument.capture());

		var message = argument.getValue();
		assertEquals(DEFAULT_SENDER, message.getFrom(), "The default sender should be used.");
	}

	@Test
	public void testThrowsErrorIfDefaultSenderIsMissing() {
		var strategy = new SmtpEmailDeliveryStrategy(mockMailSender, emailProperties);
		emailProperties.setDefaultSenderAddress(null);

		var thrown = assertThrows(UnsuccessfulDeliveryException.class, () -> {
			strategy.sendEmail(EXPECTED_RECIPIENT, EXPECTED_SUBJECT, EXPECTED_BODY);
		});

		assertTrue(thrown.getMessage().contains("default sender address is required"),
				"The error should explain why delivery failed.");
	}

	@Test
	public void testSubjectPrefix() {
		var prefix = "secure:";
		var strategy = new SmtpEmailDeliveryStrategy(mockMailSender, emailProperties);
		var argument = ArgumentCaptor.forClass(SimpleMailMessage.class);
		emailProperties.setSubjectPrefix("secure:");

		strategy.sendEmail(EXPECTED_SENDER, EXPECTED_RECIPIENT, EXPECTED_SUBJECT, EXPECTED_BODY);
		verify(mockMailSender).send(argument.capture());

		var message = argument.getValue();
		assertEquals(prefix + " " + EXPECTED_SUBJECT, message.getSubject());
	}

	@Test
	public void testExceptionConversion() {
		var expectedMessage = "Fake auth exception";
		var strategy = new SmtpEmailDeliveryStrategy(mockMailSender, emailProperties);
		doThrow(new MailAuthenticationException(expectedMessage)).when(mockMailSender)
				.send(any(SimpleMailMessage.class));

		var thrownException = assertThrows(UnsuccessfulDeliveryException.class, () -> {
			strategy.sendEmail(EXPECTED_SENDER, EXPECTED_RECIPIENT, EXPECTED_SUBJECT, EXPECTED_BODY);
		});

		var cause = thrownException.getCause();
		assertEquals(cause.getMessage(), expectedMessage);
	}

}
