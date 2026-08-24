<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# Prometheus Metric Registration Companion Changelog

## [Unreleased]

## [0.1.0]

### Added

- Warning icon on `Counter`/`Histogram`/`Gauge`/`Summary`
  `.build()....register()` built inside a regular method instead of
  a static final field -- can throw a real IllegalArgumentException
  on duplicate registration.
- 100% static text/PSI analysis, Java and Kotlin, no network calls,
  no telemetry. Free.

[Unreleased]: https://github.com/GapHunterLabs/prometheus-metric-registration-companion/compare/0.1.0...HEAD
[0.1.0]: https://github.com/GapHunterLabs/prometheus-metric-registration-companion/commits/0.1.0
