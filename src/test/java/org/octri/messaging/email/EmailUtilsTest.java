package org.octri.messaging.email;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class EmailUtilsTest {

	private static final String PREFIX = "secure:";
	private static final String INPUT_SUBJECT = "Subject";
	private static final String EXPECTED_SUBJECT = PREFIX + " " + INPUT_SUBJECT;

	@Test
	public void testAddPrefixToSubject() {
		assertEquals(EXPECTED_SUBJECT, EmailUtils.addPrefixToSubject(INPUT_SUBJECT, PREFIX),
				"The prefix is prepended to the subject string.");
	}

	@Test
	public void testAddPrefixToSubjectTrimsPrefix() {
		assertEquals(EXPECTED_SUBJECT, EmailUtils.addPrefixToSubject(INPUT_SUBJECT, " " + PREFIX),
				"Padding is trimmed before appending the prefix to the subject string.");

		assertEquals(EXPECTED_SUBJECT, EmailUtils.addPrefixToSubject(INPUT_SUBJECT, PREFIX + " "),
				"Padding is trimmed before appending the prefix to the subject.");

		assertEquals(EXPECTED_SUBJECT, EmailUtils.addPrefixToSubject(INPUT_SUBJECT, " " + PREFIX + " "),
				"Padding is trimmed before appending the prefix to the subject.");
	}

	@Test
	public void testAddPrefixToSubjectIgnoresBlankPrefix() {
		assertEquals(INPUT_SUBJECT, EmailUtils.addPrefixToSubject(INPUT_SUBJECT, ""),
				"Subject is unchanged if prefix is empty.");

		assertEquals(INPUT_SUBJECT, EmailUtils.addPrefixToSubject(INPUT_SUBJECT, "     "),
				"Subject is unchanged if prefix is blank.");

		assertEquals(INPUT_SUBJECT, EmailUtils.addPrefixToSubject(INPUT_SUBJECT, null),
				"Subject is unchanged if prefix is null.");
	}

	@Test
	public void testAddPrefixToSubjectIgnoresPrefixIfAlreadyPresent() {
		assertEquals(EXPECTED_SUBJECT, EmailUtils.addPrefixToSubject(EXPECTED_SUBJECT, PREFIX),
				"Prefix is ignored if it was already added to the subject.");
	}

}
