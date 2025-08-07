package org.octri.messaging.sms;

import java.util.function.Predicate;
import java.util.regex.Pattern;

import org.octri.messaging.exception.InvalidPhoneNumberException;

/**
 * Utilities for working with SMS messages.
 */
public class SmsUtils {

	// Matches phone numbers in E.164 format expected by Twilio
	// see: https://en.wikipedia.org/wiki/E.164
	// see: https://stackoverflow.com/a/23299989
	private static final Predicate<String> e164Predicate = Pattern.compile("^\\+[1-9]\\d{1,14}$").asMatchPredicate();

	// Matches normalized North American Numbering Plan (NANP) phone numbers
	// see: https://en.wikipedia.org/wiki/North_American_Numbering_Plan
	// see:
	// https://en.wikipedia.org/wiki/List_of_North_American_Numbering_Plan_area_codes
	private static final Predicate<String> nanpPhonePredicate = Pattern.compile("^[2-9]\\d{2}[2-9]\\d{6}$")
			.asMatchPredicate();

	/**
	 * Reports whether the given string is a valid phone number in E.164 format.
	 *
	 * @param phoneNumber
	 *            a phone number string
	 * @return true if the string is a valid E.164 phone number, false otherwise
	 */
	public static boolean isE164PhoneNumber(String phoneNumber) {
		return e164Predicate.test(phoneNumber);
	}

	/**
	 * Reports whether the given string is a valid phone number in North American Numbering Plan format. The phone
	 * number is normalized by removing non-digit characters before testing.
	 *
	 * @param phoneNumber
	 *            a phone number string
	 * @return true if the normalized string is a valid NANP phone number, false
	 *         otherwise
	 */
	public static boolean isNorthAmericanPhoneNumber(String phoneNumber) {
		return isNormalizedNorthAmericanPhoneNumber(normalizePhoneNumber(phoneNumber));
	}

	/**
	 * Reports whether the given string is a valid phone number in North American Numbering Plan (NANP) format. Input
	 * should be normalized {@link normalizePhoneNumber} first.
	 *
	 * @param phoneNumber
	 *            a phone number string
	 * @return true if the string is a valid NANP phone number, false otherwise
	 */
	public static boolean isNormalizedNorthAmericanPhoneNumber(String phoneNumber) {
		return nanpPhonePredicate.test(phoneNumber);
	}

	/**
	 * Normalizes a phone number by stripping non-digit characters.
	 *
	 * @param phoneNumber
	 *            a phone number string
	 * @return the phone number with all non-digit characters removed
	 */
	public static String normalizePhoneNumber(String phoneNumber) {
		return phoneNumber.replaceAll("[^\\d]", "");
	}

	/**
	 * Converts phone numbers in various formats to the E.164 format expected by the Twilio API. If the number is not
	 * already in E.164 format, it is assumed to be a ten-digit US or Canadian phone number with country code "1".
	 *
	 * @param phoneNumber
	 *            a string representing a US phone number
	 * @return the number in E.164 format
	 * @see <a href="https://en.wikipedia.org/wiki/E.164">https://en.wikipedia.org/wiki/E.164</a>
	 */
	public static String toE164PhoneNumber(String phoneNumber) {
		if (isE164PhoneNumber(phoneNumber)) {
			return phoneNumber;
		}

		var digitsOnly = normalizePhoneNumber(phoneNumber);
		if (!isNormalizedNorthAmericanPhoneNumber(digitsOnly)) {
			throw new InvalidPhoneNumberException(
					String.format("%s (%s) is not a valid North American phone number.", phoneNumber, digitsOnly));
		}

		return "+1" + digitsOnly;
	}

}
