package org.octri.messaging.sms;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.twilio.type.PhoneNumber;

/**
 * @see https://stackoverflow.com/questions/21787128/how-to-unit-test-jackson-jsonserializer-and-jsondeserializer
 */
public class TwilioPhoneNumberDeserializerTest {

	private static final String EXPECTED_NUMBER = "+15035551234";

	private ObjectMapper objectMapper = new ObjectMapper();
	private JsonDeserializer<PhoneNumber> deserializer;

	@BeforeEach
	public void setUp() {
		deserializer = new TwilioPhoneNumberDeserializer();
	}

	@Test
	public void testDeserializeEndpointObject() throws JsonParseException, IOException {
		var json = """
				{"endpoint":"+15035551234"}
				""";
		var output = deserializer.deserialize(getParser(json), getContext());
		assertTrue(output instanceof PhoneNumber, "Should produce a phone number object");
		assertEquals(EXPECTED_NUMBER, output.getEndpoint(), "Should produce the expected phone number");
	}

	@Test
	public void testDeserializeInvalidPhoneNumberFromEndpointObject() throws JsonParseException, IOException {
		var notE164PhoneNumber = """
				{"endpoint": "503-555-1234"}
				""";
		var notPhoneNumber = """
				{"endpoint": "NOPE"}
				""";

		var notE164Output = deserializer.deserialize(getParser(notE164PhoneNumber), getContext());
		assertNull(notE164Output, "Phone number should be null if endpoint value is not in E.164 format");

		var notPhoneNumberOutput = deserializer.deserialize(getParser(notPhoneNumber), getContext());
		assertNull(notPhoneNumberOutput, "Phone number should be null if endpoint value is not a phone number");
	}

	@Test
	public void testDeserializeNullPhoneNumberFromEndpointObject() throws JsonParseException, IOException {
		var json = """
				{"endpoint": null}
				""";
		var output = deserializer.deserialize(getParser(json), getContext());
		assertNull(output, "Phone number should be null when endpoint value is explicitly null");
	}

	@Test
	public void testDeserializeNonTextualPhoneNumberFromEndpointObject() throws JsonParseException, IOException {
		var json = """
				{"endpoint": 42}
				""";
		var output = deserializer.deserialize(getParser(json), getContext());
		assertNull(output, "Phone number should be null if endpoint value is not textual");
	}

	@Test
	public void testDeserializeNonEndpointObject() throws JsonParseException, IOException {
		var json = """
				{"nope": "no way"}
				""";
		var output = deserializer.deserialize(getParser(json), getContext());
		assertNull(output, "Phone number should be null if object node does not have an endpoint field");
	}

	@Test
	public void testDeserializePhoneNumberString() throws JsonParseException, IOException {
		var json = """
				"+15035551234"
				""";
		var output = deserializer.deserialize(getParser(json), getContext());

		assertTrue(output instanceof PhoneNumber, "Should produce a phone number object");
		assertEquals(EXPECTED_NUMBER, output.getEndpoint(), "Should produce the expected phone number");
	}

	@Test
	public void testDeserializeNullPhoneNumberValue() throws JsonParseException, IOException {
		var output = deserializer.deserialize(getParser("null"), getContext());
		assertNull(output, "Phone number should be null if node is an explicit null value");
	}

	@Test
	public void testDeserializeInvalidPhoneNumberValue() throws JsonParseException, IOException {
		var notE164PhoneNumber = """
				"503-555-1234"
				""";
		var notPhoneNumber = """
				"NOPE"
				""";

		var notE164Output = deserializer.deserialize(getParser(notE164PhoneNumber), getContext());
		assertNull(notE164Output, "Phone number should be null if string value is not in E.164 format");

		var notPhoneNumberOutput = deserializer.deserialize(getParser(notPhoneNumber), getContext());
		assertNull(notPhoneNumberOutput, "Phone number should be null if string value is not a phone number");
	}

	@Test
	public void testDeserializeNonTextualValue() throws JsonParseException, IOException {
		var output = deserializer.deserialize(getParser("42"), getContext());
		assertNull(output, "Phone number should be null if node is a non-textual value");
	}

	private JsonParser getParser(String json) throws JsonParseException, IOException {
		return objectMapper.getFactory().createParser(json);
	}

	private DeserializationContext getContext() {
		return objectMapper.getDeserializationContext();
	}

}
