package dev.gaphunter.prometheusmetricregistrationcompanion.detect

import com.intellij.testFramework.fixtures.BasePlatformTestCase

class JavaMetricRegistrationFinderTest : BasePlatformTestCase() {

    fun `test Counter built and registered inside a regular method is flagged`() {
        val file = myFixture.configureByText(
            "OrderService.java",
            """
            class OrderService {
                void placeOrder() {
                    Counter requests = Counter.build().name("requests_total").help("Total requests.").register();
                    requests.inc();
                }
            }
            """.trimIndent(),
        )
        assertEquals(1, JavaMetricRegistrationFinder.findAll(file).size)
    }

    fun `test Histogram built and registered inside a regular method is flagged`() {
        val file = myFixture.configureByText(
            "OrderService.java",
            """
            class OrderService {
                void placeOrder() {
                    Histogram latency = Histogram.build().name("latency_seconds").help("Latency.").register();
                }
            }
            """.trimIndent(),
        )
        assertEquals(1, JavaMetricRegistrationFinder.findAll(file).size)
    }

    fun `test construction as a static field initializer is not flagged`() {
        val file = myFixture.configureByText(
            "OrderService.java",
            """
            class OrderService {
                static final Counter requests = Counter.build().name("requests_total").help("Total requests.").register();

                void placeOrder() {
                    requests.inc();
                }
            }
            """.trimIndent(),
        )
        assertTrue(JavaMetricRegistrationFinder.findAll(file).isEmpty())
    }

    fun `test unrelated register call is not flagged`() {
        val file = myFixture.configureByText(
            "OrderService.java",
            """
            class OrderService {
                void setup() {
                    EventBus.build().withHandler(this).register();
                }
            }
            """.trimIndent(),
        )
        assertTrue(JavaMetricRegistrationFinder.findAll(file).isEmpty())
    }
}
