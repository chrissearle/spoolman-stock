package net.chrissearle

import arrow.core.raise.either
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.call.body
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import io.ktor.utils.io.ByteReadChannel
import net.chrissearle.spoolman.SpoolLabel
import net.chrissearle.spoolman.SpoolmanService
import net.chrissearle.spoolman.StockSummary
import net.chrissearle.spoolman.configureSpoolmanRouting

class LabelTest :
    FunSpec({

        test("Label Fetching") {
            val engine =
                MockEngine {
                    respond(
                        content = ByteReadChannel(loadFixture("/spools_export.json")),
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                }

            testApplication {
                buildTestApplication(engine)

                val client = buildTestClient()

                client
                    .get("/stock/api/spools") {
                        accept(ContentType.Application.Json)
                    }.apply {
                        status shouldBe HttpStatusCode.OK

                        val response = body<List<SpoolLabel>>()

                        response.size shouldBe 78
                    }
            }
        }
    })

private fun buildService(engine: MockEngine) =
    SpoolmanService(
        spoolsUrl = "/spools",
        filamentsUrl = "/filaments",
        httpClient = buildClient(engine),
        viewPrefix = ""
    )

private fun ApplicationTestBuilder.buildTestApplication(engine: MockEngine) {
    serializedTestApplication {
        configureSpoolmanRouting(
            buildService(engine)
        )
    }
}
