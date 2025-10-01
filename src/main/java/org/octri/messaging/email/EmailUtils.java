package org.octri.messaging.email;

import org.apache.commons.lang3.StringUtils;

/**
 * Utilities for working with email messages.
 */
public class EmailUtils {

	/**
	 * Adds a prefix to the given message subject, if the prefix is defined and is not already present.
	 *
	 * @param messageSubject
	 *            email message subject
	 * @param prefix
	 *            optional prefix string
	 * @return the message subject with the prefix added, or the original message subject
	 */
	public static String addPrefixToSubject(String messageSubject, String prefix) {
		var trimmedPrefix = StringUtils.trimToEmpty(prefix);
		return StringUtils.isNotBlank(trimmedPrefix) && !messageSubject.startsWith(trimmedPrefix)
				? trimmedPrefix + " " + messageSubject
				: messageSubject;
	}

}
