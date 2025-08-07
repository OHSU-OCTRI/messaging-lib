package org.octri.messaging.autoconfig;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * Custom condition that detects if the Twilio API has been configured.
 */
public class TwilioConfiguredCondition implements Condition {

	private static final Logger log = LoggerFactory.getLogger(TwilioConfiguredCondition.class);

	@Override
	public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
		var env = context.getEnvironment();
		var accountSid = env.getProperty("octri.messaging.twilio.account-sid");
		var authToken = env.getProperty("octri.messaging.twilio.auth-token");
		var enabled = StringUtils.isNotBlank(accountSid) && StringUtils.isNotBlank(authToken);
		log.debug("Checking for Twilio: accountSid = {} authToken = {}", accountSid, authToken);
		log.debug("Twilio enabled: {}", enabled);
		return enabled;
	}

}
