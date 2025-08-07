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
