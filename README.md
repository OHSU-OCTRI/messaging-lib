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
octri.messaging.twilio.auth-token|string|None|The Twilio OAuth token. Only required if SMS delivery method is TWILIO|
octri.messaging.twilio.callback-url|string|None|The application callback url for getting status updates on a message delivery. This is optional.|

