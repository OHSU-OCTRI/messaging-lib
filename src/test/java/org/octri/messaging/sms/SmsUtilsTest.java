package org.octri.messaging.sms;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.octri.messaging.exception.InvalidPhoneNumberException;

public class SmsUtilsTest {

	private static final String E164_NUMBER = "+15035551234";
	private static final String MEXICO_E164_NUMBER = "+525035551234";
	private static final List<String> exampleFormats = Arrays.asList("5035551234", "503-555-1234", "(503) 555-1234",
			"503 555 1234", "503.555.1234");

	@Test
	public void testIsE164PhoneNumber() {
		assertTrue(SmsUtils.isE164PhoneNumber(E164_NUMBER), "should be true for E.164 numbers");
		assertTrue(SmsUtils.isE164PhoneNumber(MEXICO_E164_NUMBER), "should be true for other country codes (Mexico)");

		assertFalse(SmsUtils.isE164PhoneNumber(""), "should be false for empty string");
		assertFalse(SmsUtils.isE164PhoneNumber(E164_NUMBER + "99999"),
				"should be false for numbers with > 15 total digits");
		for (String format : exampleFormats) {
			assertFalse(SmsUtils.isE164PhoneNumber(format), "should be false for common non-E.164 formats");
		}
	}

	@Test
	public void testIsNorthAmericanPhoneNumber() {
		assertTrue(SmsUtils.isNorthAmericanPhoneNumber("5035551234"),
				"should be true for normalized NANP phone numbers");
		for (String format : exampleFormats) {
			assertTrue(SmsUtils.isNorthAmericanPhoneNumber(format), "should be true for common formats: " + format);
		}
		assertFalse(SmsUtils.isNorthAmericanPhoneNumber("003-555-1234"),
				"should be false if area code is invalid (first digit < 2)");
		assertFalse(SmsUtils.isNorthAmericanPhoneNumber("103.555.1234"),
				"should be false if area code is invalid (first digit < 2)");
		assertFalse(SmsUtils.isNorthAmericanPhoneNumber("503-055-1234"),
				"should be false if central office code is invalid (first digit < 2)");
		assertFalse(SmsUtils.isNorthAmericanPhoneNumber("503-155-1234"),
				"should be false if central office code is invalid (first digit < 2)");
		assertFalse(SmsUtils.isNorthAmericanPhoneNumber(MEXICO_E164_NUMBER),
				"should be false for non-NANP phone numbers");
	}

	@Test
	public void testIsNormalizedNorthAmericanPhoneNumber() {
		assertTrue(SmsUtils.isNormalizedNorthAmericanPhoneNumber("2035551234"),
				"should be true for normalized NANP phone numbers");
		assertFalse(SmsUtils.isNormalizedNorthAmericanPhoneNumber("0035551234"),
				"should be false if area code is invalid (first digit < 2)");
		assertFalse(SmsUtils.isNormalizedNorthAmericanPhoneNumber("1035551234"),
				"should be false if area code is invalid (first digit < 2)");
		assertFalse(SmsUtils.isNorthAmericanPhoneNumber("5030551234"),
				"should be false if central office code is invalid (first digit < 2)");
		assertFalse(SmsUtils.isNorthAmericanPhoneNumber("5031551234"),
				"should be false if central office code is invalid (first digit < 2)");
		assertFalse(SmsUtils.isNormalizedNorthAmericanPhoneNumber("503-555-1234"),
				"should be false for numbers that have not been normalized");
		assertFalse(SmsUtils.isNormalizedNorthAmericanPhoneNumber(MEXICO_E164_NUMBER),
				"should be false for non-NANP phone numbers");
	}

	@Test
	public void normalizePhoneNumber() {
		assertEquals("5035551234", SmsUtils.normalizePhoneNumber("\t+503.555 1234   "),
				"non-digit characters are removed");
		for (String format : exampleFormats) {
			assertEquals("5035551234", SmsUtils.normalizePhoneNumber(format),
					"common formats should convert as expected: " + format);
		}
	}

	@Test
	public void testE164Conversion() {
		assertEquals(E164_NUMBER, SmsUtils.toE164PhoneNumber(E164_NUMBER), "E.164 format is unchanged");
		assertEquals(MEXICO_E164_NUMBER, SmsUtils.toE164PhoneNumber(MEXICO_E164_NUMBER),
				"Foreign numbers in E.164 format are unchanged");

		for (String format : exampleFormats) {
			assertEquals(E164_NUMBER, SmsUtils.toE164PhoneNumber(format), "Common formats should convert as expected");
		}

		assertThrows(InvalidPhoneNumberException.class, () -> {
			SmsUtils.toE164PhoneNumber("555-1234");
		}, "Should throw an exception for phone numbers with < 10 digits.");
		assertThrows(InvalidPhoneNumberException.class, () -> {
			SmsUtils.toE164PhoneNumber("55512345");
		}, "Should throw an exception for phone numbers with > 10 digits.");
		assertThrows(InvalidPhoneNumberException.class, () -> {
			SmsUtils.toE164PhoneNumber("1035551234");
		}, "Should throw an exception for phone numbers with invalid NANP area codes.");
	}
}
