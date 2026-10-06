<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# Prometheus Metric Registration Companion Changelog

## [Unreleased]

### Changed

- The rating prompt's local counter keeps one-way fingerprints of findings
  instead of their file paths, and deletes the list that earlier versions
  kept.
- `PRIVACY.md` describes the values the plugin keeps in the IDE's local
  settings.

## [0.1.1]

### Fixed

- Review/star CTA now links to this plugin's own Marketplace
  reviews page instead of the vendor's generic plugin list.

## [0.1.0]

### Added

- Warning icon on `Counter`/`Histogram`/`Gauge`/`Summary`
  `.build()....register()` built inside a regular method instead of
  a static final field -- can throw a real IllegalArgumentException
  on duplicate registration.
- 100% static text/PSI analysis, Java and Kotlin, no network calls,
  no telemetry. Free.

[Unreleased]: https://github.com/GapHunterLabs/prometheus-metric-registration-companion/compare/0.1.1...HEAD
[0.1.1]: https://github.com/GapHunterLabs/prometheus-metric-registration-companion/compare/0.1.0...0.1.1
[0.1.0]: https://github.com/GapHunterLabs/prometheus-metric-registration-companion/commits/0.1.0
