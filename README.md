# Prometheus Metric Registration Companion

Warning icon on a `Counter.build()....register()` (also `Histogram`/
`Gauge`/`Summary`, Prometheus's Java client's four metric types)
construction written inside a regular method body — the client's own
official documentation states "the best way to register a metric is
via a static final class variable". Registration happens against the
process-global default registry: calling `.register()` more than once
for the same metric name throws a real `IllegalArgumentException`
("Collector already registered that provides name...") at runtime,
not just a style nit.

## Why it exists

`Counter.build().name("requests_total").help("Total requests.").register()`
compiles fine — call the method that contains it twice (a request
handler, say) and the second call crashes with a real runtime
exception, because Prometheus registers metrics globally by name and
refuses duplicates.

## Why built this way

- **100% static text/PSI analysis** — matches by simple text, so it
  works whether the real Prometheus client jar is on the classpath or
  not. Java and Kotlin.

## v0.1 scope — stated honestly, not exhaustively

Only flags the "build from scratch inside a regular method" shape —
a metric stored as a static final field (the documented correct
pattern) is never flagged.

## Usage

Open any Java/Kotlin file using the Prometheus Java client. A metric
built inside a regular method shows a warning icon.

## Enterprise / Team Licensing

Need enterprise features, custom rules, or team licensing? Contact us at
**gaphunterlabs@gmail.com**.

## Development

```
./gradlew test           # unit tests
./gradlew buildPlugin    # generates build/distributions/*.zip
./gradlew verifyPlugin   # checks compatibility against real IDEs
```

## License

Apache-2.0. See `LICENSE`.
