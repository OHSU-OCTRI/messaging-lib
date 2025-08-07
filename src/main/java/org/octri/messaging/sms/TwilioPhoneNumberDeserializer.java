package org.octri.messaging.sms;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ValueNode;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

/**
 * Custom deserializer that is able to deserialize the JSON produced when Jackson serializes Twilio {@link PhoneNumber}
 * objects. Jackson serializes these as a JSON object of the form <code>{"endpoint":"+15035551234"}</code>, which is not
 * successfully deserialized back to a {@link PhoneNumber}. Adding this deserializer to the {@link ObjectMapper} allows
 * <code>Message.fromJson</code> to successfully deserialize {@link Message} objects serialized to JSON.
 */
public class TwilioPhoneNumberDeserializer extends StdDeserializer<PhoneNumber> {

	private static final Logger log = LoggerFactory.getLogger(TwilioPhoneNumberDeserializer.class);

	/**
	 * Constructor.
	 */
	public TwilioPhoneNumberDeserializer() {
		super(PhoneNumber.class);
	}

	/**
	 * Attempts to convert the given JSON to a Twilio {@link PhoneNumber}. The JSON may be either an object with an
	 * endpoint field containing a phone number in E.164 format (e.g. <code>{"endpoint":"+15035551234"}</code>) or a
	 * string value containing a phone number in E.164 format (e.g <code>"+15035551234"</code>). All other JSON will be
	 * deserialized to null.
	 *
	 * @param p
	 *            JSON parser
	 * @param ctxt
	 *            deserialization context
	 * @throws IOException
	 *             if there are low-level read issues
	 * @throws JacksonException
	 *             if there are JSON decoding issues
	 */
	@Override
	public PhoneNumber deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
		var node = p.getCodec().readTree(p);
		log.debug("Field name: {}", p.currentName());
		log.debug("JSON node: {}", node);

		var phoneNumber = findTextValue(node);
		log.debug("Extracted phone number value: {}", phoneNumber);

		if (phoneNumber != null && SmsUtils.isE164PhoneNumber(phoneNumber)) {
			return new PhoneNumber(phoneNumber);
		}

		return null;
	}

	private String findTextValue(TreeNode node) {
		Assert.notNull(node, "JSON node should not be null");
		ValueNode valueNode = null;

		if (node.isObject()) {
			var endpointFieldNode = node.get("endpoint");
			if (endpointFieldNode != null && endpointFieldNode.isValueNode()) {
				valueNode = (ValueNode) endpointFieldNode;
			}
		} else {
			valueNode = (ValueNode) node;
		}

		if (valueNode == null) {
			log.error("Could not extract phone number value from JSON node: {}", node);
		} else if (!valueNode.isTextual()) {
			log.error("Unexpected phone number value in JSON node: {}", node);
		} else {
			return valueNode.asText();
		}

		return null;
	}

}
