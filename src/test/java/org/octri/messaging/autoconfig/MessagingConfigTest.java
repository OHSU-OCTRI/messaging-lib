package org.octri.messaging.autoconfig;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

@ExtendWith(OutputCaptureExtension.class)
public class MessagingConfigTest {

	@Test
	public void testValidatePropertiesDoesNotWarnWhenDefaultSenderAddressSet(CapturedOutput output) {
		MessagingProperties properties = new MessagingProperties();
		properties.getEmail().setDefaultSenderAddress("test@example.com");
		new MessagingConfig(properties);
		assertFalse(output.getOut().contains("default-sender-address property is blank"),
				"Output should not include warning about missing default-sender-address");
	}

	@Test
	public void testValidatePropertiesWarnsWhenDefaultSenderAddressMissing(CapturedOutput output) {
		MessagingProperties properties = new MessagingProperties();
		new MessagingConfig(properties);
		assertTrue(output.getOut().contains("default-sender-address property is blank"),
				"Output should include warning about missing default-sender-address");
	}

	@Test
	public void testValidatePropertiesDoesNotThrowWhenEmailConfigMissing() {
		MessagingProperties properties = new MessagingProperties();
		properties.setEmail(null);

		Assertions.assertDoesNotThrow(() -> {
			new MessagingConfig(properties);
		}, "validateProperties should not throw an exception when email config is not explicitly set");
	}

}
