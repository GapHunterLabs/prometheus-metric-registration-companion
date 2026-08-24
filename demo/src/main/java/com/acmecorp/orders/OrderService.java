package com.acmecorp.orders;

import io.prometheus.client.Counter;

/**
 * Demo data for Prometheus Metric Registration Companion — used with
 * `./gradlew runIde` to capture the real Marketplace screenshot. Open
 * this file, the warning icon should appear on the call inside
 * `placeOrder`.
 */
public class OrderService {

    static final Counter sharedRequests = Counter.build()
        .name("orders_total")
        .help("Total orders placed.")
        .register();

    public void placeOrder() {
        // Built here on every call -- throws IllegalArgumentException
        // the second time this method runs. FLAGGED.
        Counter requests = Counter.build()
            .name("orders_total")
            .help("Total orders placed.")
            .register();
        requests.inc();
    }
}
