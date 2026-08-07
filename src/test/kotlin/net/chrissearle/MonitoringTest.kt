package net.chrissearle

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import io.mockk.mockk

class MonitoringTest :
    FunSpec({
        test("metrics are not exposed") {
            testApplication {
                application {
                    configureMonitoring(mockk<UpstreamHealthCheck>())
                }

                client.get("/api/metrics").status shouldBe HttpStatusCode.NotFound
            }
        }
    })
