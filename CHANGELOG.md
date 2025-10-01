# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Changed

- Publish -SNAPSHOT releases to Maven Central.

## [0.2.0] - 2025-10-01

### Added

- README documentation on configurable properties. (CIS-3349)
- README documentation on SMTP and Twilio delivery strategies. (AUTHLIB-162)
- Add configuration property for default email sender address. (CIS-3351)
- Add configuration property for email subject prefix. (CIS-3351)
- Prepend email subject prefix to the subject of outgoing email messages. (CIS-3351)
- **Breaking**: Add a method to the `EmailDeliveryStrategy` interface that sends email using the default sender address. (CIS-3351)

### Fixed

- Use project name to construct Maven Central deployment names. (#3)

## [0.1.0] - 2025-08-12

Initial release of code extracted from OPEN.

### Added

- Add initial implementation extracted from OPEN. (CIS-3204)
- Add Spring Boot auto-configuration. (CIS-3204)

[unreleased]: https://github.com/OHSU-OCTRI/messaging-lib/compare/v0.2.0...HEAD
[0.2.0]: https://github.com/OHSU-OCTRI/messaging-lib/compare/v0.1.0...v0.2.0
[0.1.0]: https://github.com/OHSU-OCTRI/messaging-lib/compare/d8d68641086e30da918c0bba5926dc49bbafd4a8...v0.1.0
