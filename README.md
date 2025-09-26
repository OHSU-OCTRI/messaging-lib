# OCTRI Messaging Library

This package contains classes useful for interacting with messaging systems in Spring-based web applications, including:

* Utility methods for sending email and SMS messages
* Configurable delivery strategies, including
  * SMTP email delivery using Spring Boot's default `JavaMailSender` support
  * Mock email delivery to the application log
  * SMS message delivery using [Twilio](https://www.twilio.com/en-us)
  * Mock SMS delivery to the application log
* Utilities for validating and converting US phone numbers

## Using This Package

To use this package, add it to your `pom.xml` file.

```xml
	<dependency>
		<groupId>org.octri.messaging</groupId>
		<artifactId>messaging_lib</artifactId>
		<version>${messaging_lib.version}</version>
	</dependency>
```

## Implementation

The library is implemented using [Spring Boot](https://spring.io/projects/spring-boot). For a detailed list of dependencies, see [pom.xml](./pom.xml).

### Configurable Properties

Configure the following properties for your applications's use case:

| Property | Type | Default Value | Description |
|---|---|---|---|
|octri.messaging.enabled|boolean|TRUE|Whether the messaging library is enabled.|
|octri.messaging.email-delivery-method|enum|LOG|Dictates how emails will be sent. Options are LOG (log without sending). NOOP (do nothing), and SMTP (send via SMTP)|
|octri.messaging.sms-delivery-method|enum|LOG|Dictates how texts will be sent. Options are LOG (log without sending). NOOP (do nothing), and TWILIO (send via Twilio)|
|octri.messaging.twilio.account-sid|string|None|The Twilio account sid. Only required if SMS delivery method is TWILIO|
|octri.messaging.twilio.auth-token|string|None|The Twilio OAuth token. Only required if SMS delivery method is TWILIO|
|octri.messaging.twilio.callback-url|string|None|The application callback url for getting status updates on a message delivery. This is optional.|

### Email Delivery Using SMTP

The library's [`SmtpEmailDeliveryStrategy`](./src/main/java/org/octri/messaging/email/SmtpEmailDeliveryStrategy.java) delivers messages using Spring's built-in support for email. To enable this strategy, set `octri.messaging.email-delivery-method=SMTP` and provide a `JavaMailSender` bean to deliver the messages. In a Spring Boot application, the needed `JavaMailSender` bean is automatically instantiated by setting the appropriate `spring.mail` configuration properties. See the Spring documentation for more information and a full list of configuration properties.

* [Spring Boot Reference Documentation: Sending Email](https://docs.spring.io/spring-boot/reference/io/email.html)
* [Spring Boot Documentation: Mail Properties](https://docs.spring.io/spring-boot/appendix/application-properties/index.html#appendix.application-properties.mail)
* [Spring Framework Reference Documentation: Email Integration](https://docs.spring.io/spring-framework/reference/integration/email.html)

### SMS Delivery Using Twilio

The library's [`TwilioSmsDeliveryStrategy`](./src/main/java/org/octri/messaging/sms/TwilioSmsDeliveryStrategy.java) delivers SMS messages using the [Twilio](https://www.twilio.com/en-us) API. To use this strategy, you will need a Twilio account SID and auth token (available on the [Twilio console](https://twilio.com/console)). In addition, you will also need to [purchase an SMS number](https://www.twilio.com/console/phone-numbers/search) and complete [A2P 10DLC registration] or [toll-free verification] if you will deliver messages to phone numbers in the United States or Canada.

Once you have the Twilio account SID, auth token, and SMS number, you can enable the Twilio delivery strategy by setting the following parameters:

```properties
octri.messaging.sms-delivery-method=TWILIO
octri.messaging.twilio.account-sid=YOUR_ACCOUNT_SID
octri.messaging.twilio.auth-token=YOUR_AUTH_TOKEN
```

When sending SMS messages, pass your SMS number in [E.164 format](https://en.wikipedia.org/wiki/E.164) to the [`sendSms` method](./src/main/java/org/octri/messaging/sms/SmsDeliveryStrategy.java) (`fromNumber` parameter). Delivery will fail if the number is not a Twilio SMS number in E.164 format or the number has not received necessary regulatory approvals ([A2P 10DLC registration] or [toll-free verification]).

[A2P 10DLC registration]: (https://help.twilio.com/articles/1260801864489-How-do-I-register-to-use-A2P-10DLC-messaging)
[toll-free verification]: (https://help.twilio.com/articles/5377174717595-Toll-Free-Message-Verification-for-US-Canada)