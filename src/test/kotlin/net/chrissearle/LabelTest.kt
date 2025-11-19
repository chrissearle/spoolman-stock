package net.chrissearle

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
import net.chrissearle.spoolman.ApiConfig
import net.chrissearle.spoolman.SpoolmanApi
import net.chrissearle.spoolman.configureSpoolmanRouting
import net.chrissearle.spoolman.model.SpoolLabel
import net.chrissearle.spoolman.spoolmanService

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

        test("Location Fetching") {
            val engine =
                MockEngine {
                    respond(
                        content = ByteReadChannel(loadFixture("/locations.json")),
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                }

            testApplication {
                buildTestApplication(engine)

                val client = buildTestClient()

                client
                    .get("/stock/api/locations") {
                        accept(ContentType.Application.Json)
                    }.apply {
                        status shouldBe HttpStatusCode.OK

                        val response = body<List<String>>()

                        response.size shouldBe 15
                    }
            }
        }
    })

private fun buildService(engine: MockEngine) =
    spoolmanService(
        spoolmanApi =
            SpoolmanApi(
                httpClient = buildClient(engine),
                apiConfig = ApiConfig("/")
            ),
        spoolPrefix = "",
        locationPrefix = "",
        startLocations = emptyList()
    )

private fun ApplicationTestBuilder.buildTestApplication(engine: MockEngine) {
    serializedTestApplication {
        configureSpoolmanRouting(
            buildService(engine)
        )
    }
}
