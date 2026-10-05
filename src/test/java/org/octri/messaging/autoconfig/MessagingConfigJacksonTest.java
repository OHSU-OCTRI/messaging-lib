package org.octri.messaging.autoconfig;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.octri.messaging.sms.TwilioHelper;
import org.octri.test.messaging.TwilioTestUtils;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.jackson.autoconfigure.JacksonAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;

import tools.jackson.databind.json.JsonMapper;

/**
 * Verifies that the {@link TwilioHelper} created by {@link MessagingConfig} keeps using its own Jackson 2 object
 * mapper when the application also configures Spring Boot's Jackson 3 {@link JsonMapper}.
 * 
 * TODO: Refactor or remove after upgrading to Twilio version that supports Jackson 3
 */
public class MessagingConfigJacksonTest {

	/**
	 * Naming strategies apply to POJO properties, so a record is used to verify Spring's settings.
	 */
	record SampleValue(int someField) {
	}

	private static Message queuedMessage;

	// Dummy application context which autoconfigures Jackson 3 from Spring Boot 4
	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
			.withConfiguration(AutoConfigurations.of(JacksonAutoConfiguration.class, MessagingConfig.class))
			.withPropertyValues(
					"octri.messaging.twilio.account-sid=mockSid",
					"octri.messaging.twilio.auth-token=mockToken",
					// Configured to distinguish application and library behaviors
					"spring.jackson.property-naming-strategy=SNAKE_CASE",
					"spring.jackson.serialization.indent-output=true");

	private MockedStatic<Twilio> mockTwilio;

	@BeforeAll
	public static void init() throws IOException {
		queuedMessage = TwilioTestUtils.getQueuedMessage();
	}

	@BeforeEach
	public void setup() {
		mockTwilio = Mockito.mockStatic(Twilio.class);
	}

	@AfterEach
	public void tearDown() {
		mockTwilio.close();
	}

	@Test
	public void testTwilioHelperIgnoresSpringJacksonConfiguration() {
		contextRunner.run(context -> {
			// Preconditions:
			// - Spring Boot's Jackson 3 auto-configuration created its JsonMapper
			// - The ApplicationContextRunner has been correctly configured for messaging-lib tests
			assertThat(context).hasSingleBean(JsonMapper.class);
			assertThat(context).hasSingleBean(TwilioHelper.class);

			// Tests that Spring's JsonMapper configurations are used, but not within the TwilioHelper class
			var springJson = context.getBean(JsonMapper.class).writeValueAsString(new SampleValue(1));
			assertTrue(springJson.contains("some_field"),
					"Spring's configured naming strategy is not being respected: " + springJson);
			assertTrue(springJson.contains("\n"),
					"Spring's configured indentation is not being respected: " + springJson);
			var helperJson = context.getBean(TwilioHelper.class).serializeMessageToJson(queuedMessage);
			assertFalse(helperJson.contains("\n"),
					"Indented JSON indicates that TwilioHelper is resolving Spring's JsonMapper: " + helperJson);
			assertFalse(helperJson.contains("account_sid"),
					"Snake case property names indicate that TwilioHelper is resolving Spring's JsonMapper: "
							+ helperJson);
		});
	}

	/**
	 * Test uses the default behavior of Jackson 2 (serialization of datetimes as numeric Unix time) to verify that
	 * TwilioHelper correctly resolves ObjectMapper to Jackson 2.
	 */
	@Test
	public void testTwilioHelperSerializesDatesAsNumeric() {
		contextRunner.run(context -> {
			var helperJson = context.getBean(TwilioHelper.class).serializeMessageToJson(queuedMessage);
			var tree = new ObjectMapper().readTree(helperJson);
			assertNotNull(tree.get("dateCreated"), "Twilio helper JSON should include dateCreated: " + helperJson);
			assertTrue(tree.get("dateCreated").isNumber(),
					"Twilio helper JSON should use numeric timestamps: " + helperJson);
		});
	}
}
