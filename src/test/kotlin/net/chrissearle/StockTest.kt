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
import net.chrissearle.spoolman.ApiConfig
import net.chrissearle.spoolman.SpoolmanApi
import net.chrissearle.spoolman.StockSummary
import net.chrissearle.spoolman.configureSpoolmanRouting
import net.chrissearle.spoolman.spoolmanApi
import net.chrissearle.spoolman.spoolmanService

class StockTest :
    FunSpec({

        test("Filament Fetching") {
            val engine =
                MockEngine {
                    respond(
                        content = ByteReadChannel(loadFixture("/filaments_export.json")),
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                }

            testApplication {
                val api = buildApi(engine)

                buildTestApplication(api)

                either {
                    val filaments = api.fetchFilaments()

                    filaments.size shouldBe 67
                }
            }
        }

        test("Spools Fetching") {
            val engine =
                MockEngine {
                    respond(
                        content = ByteReadChannel(loadFixture("/spools_export.json")),
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                }

            testApplication {
                val api = buildApi(engine)

                buildTestApplication(api)

                either {
                    val spools = api.fetchFilaments()

                    spools.size shouldBe 78
                }
            }
        }

        test("Stock Fetching") {
            val engine =
                MockEngine { request ->
                    if (request.url.encodedPath.contains("filaments")) {
                        respond(
                            content = ByteReadChannel(loadFixture("/filaments_export.json")),
                            status = HttpStatusCode.OK,
                            headers = headersOf(HttpHeaders.ContentType, "application/json")
                        )
                    } else {
                        respond(
                            content = ByteReadChannel(loadFixture("/spools_export.json")),
                            status = HttpStatusCode.OK,
                            headers = headersOf(HttpHeaders.ContentType, "application/json")
                        )
                    }
                }

            testApplication {
                val api = buildApi(engine)

                buildTestApplication(api)

                val client = buildTestClient()

                client
                    .get("/stock/api/stock") {
                        accept(ContentType.Application.Json)
                    }.apply {
                        status shouldBe HttpStatusCode.OK

                        val response = body<List<StockSummary>>()

                        response.size shouldBe 14
                    }
            }
        }
    })

private fun buildApi(engine: MockEngine) =
    spoolmanApi(
        httpClient = buildClient(engine),
        apiConfig = ApiConfig("/"),
    )

private fun buildService(api: SpoolmanApi) =
    spoolmanService(
        spoolmanApi = api,
        spoolPrefix = "",
        locationPrefix = "",
    )

private fun ApplicationTestBuilder.buildTestApplication(api: SpoolmanApi) {
    serializedTestApplication {
        configureSpoolmanRouting(
            buildService(api)
        )
    }
}
