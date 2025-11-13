package net.chrissearle

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication

class MetricsTest :
    FunSpec({
        test("Metrics are provided") {
            testApplication {
                application {
                    configureMonitoring()
                }

                client.get("/stock/api/metrics").apply {
                    status shouldBe HttpStatusCode.OK

                    val bodyText = bodyAsText()

                    bodyText shouldContain "HELP"
                    bodyText shouldContain "TYPE"
                    bodyText shouldContain "jvm_"
                }
            }
        }
    })
