package dev.gaphunter.prometheusmetricregistrationcompanion.detect

import com.intellij.testFramework.fixtures.BasePlatformTestCase

class KotlinMetricRegistrationFinderTest : BasePlatformTestCase() {

    fun `test Counter built and registered inside a regular method is flagged`() {
        val file = myFixture.configureByText(
            "OrderService.kt",
            """
            class OrderService {
                fun placeOrder() {
                    val requests = Counter.build().name("requests_total").help("Total requests.").register()
                    requests.inc()
                }
            }
            """.trimIndent(),
        )
        assertEquals(1, KotlinMetricRegistrationFinder.findAll(file).size)
    }

    fun `test Gauge built and registered inside a regular method is flagged`() {
        val file = myFixture.configureByText(
            "OrderService.kt",
            """
            class OrderService {
                fun placeOrder() {
                    val queueSize = Gauge.build().name("queue_size").help("Queue size.").register()
                }
            }
            """.trimIndent(),
        )
        assertEquals(1, KotlinMetricRegistrationFinder.findAll(file).size)
    }

    fun `test construction as a class property initializer is not flagged`() {
        val file = myFixture.configureByText(
            "OrderService.kt",
            """
            class OrderService {
                private val requests = Counter.build().name("requests_total").help("Total requests.").register()

                fun placeOrder() {
                    requests.inc()
                }
            }
            """.trimIndent(),
        )
        assertTrue(KotlinMetricRegistrationFinder.findAll(file).isEmpty())
    }

    fun `test unrelated register call is not flagged`() {
        val file = myFixture.configureByText(
            "OrderService.kt",
            """
            class OrderService {
                fun setup() {
                    EventBus.build().withHandler(this).register()
                }
            }
            """.trimIndent(),
        )
        assertTrue(KotlinMetricRegistrationFinder.findAll(file).isEmpty())
    }
}
