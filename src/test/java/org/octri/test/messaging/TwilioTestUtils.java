package org.octri.test.messaging;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.springframework.core.io.ClassPathResource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.twilio.rest.api.v2010.account.Message;

public class TwilioTestUtils {

	private static final ObjectMapper objectMapper = new ObjectMapper();

	static {
		objectMapper.registerModule(new JavaTimeModule());
	}

	public static Message getQueuedMessage() throws IOException {
		return Message.fromJson(getJsonStream("queued.json"), objectMapper);
	}

	public static Message getDeliveredMessage() throws IOException {
		return Message.fromJson(getJsonStream("delivered.json"), objectMapper);
	}

	public static Message getFailedMessage() throws IOException {
		return Message.fromJson(getJsonStream("failed.json"), objectMapper);
	}

	public static Message getUndeliveredMessage() throws IOException {
		return Message.fromJson(getJsonStream("undelivered.json"), objectMapper);
	}

	public static InputStream getJsonStream(String filename) throws IOException {
		return new ClassPathResource("twilio-examples/" + filename).getInputStream();
	}

	public static String getJsonText(String filename) throws IOException {
		var jsonStream = getJsonStream(filename);
		return new String(jsonStream.readAllBytes(), StandardCharsets.UTF_8);
	}

}
