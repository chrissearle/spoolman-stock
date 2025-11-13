package net.chrissearle.spoolman

import arrow.core.raise.Raise
import arrow.core.raise.ensure
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.headers
import io.ktor.client.request.request
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import io.ktor.server.application.Application
import net.chrissearle.api.ApiError
import net.chrissearle.api.ErrorResponse
import net.chrissearle.api.SpoolmanCallFailed
import net.chrissearle.confStr

private val logger = KotlinLogging.logger {}

class SpoolmanService(
    val apiUrl: String,
    val httpClient: HttpClient,
) {
    context(raise: Raise<ApiError>)
    suspend fun fetchStock() =
        fetch()
            .filter { !it.archived }
            .filter { it.stock != null }
            .filter { it.stock!! > 0 }
            .map { it.copy(shopUrl = it.shopUrl.normalizeShopUrl()) }
            .filter { !it.shopUrl.isNullOrBlank() }
            .filter { !it.filamentColor.isNullOrBlank() }
            .groupBy { it.filamentId }
            .map { (_, group) ->
                group.first().let {
                    StockSummary(
                        shop = it.shopUrl!!,
                        stock = it.stock!!,
                        count = group.size,
                        color = it.filamentColor!!.color(),
                        unopened = group.count { spool -> !spool.started() },
                        name = it.filamentName,
                        material = it.filamentMaterial,
                        vendor = it.filamentVendor,
                    )
                }
            }

    context(raise: Raise<ApiError>)
    suspend fun fetch() =
        httpClient
            .request(apiUrl) {
                headers {
                    append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    append(HttpHeaders.Accept, ContentType.Application.Json.toString())
                }
            }.valid()
            .body<List<Spool>>()

    context(raise: Raise<ApiError>)
    private suspend fun HttpResponse.valid(): HttpResponse {
        raise.ensure(this.status.isSuccess()) {
            val upstreamBody = this.body<String>()

            logger.warn { "Failed to fetch data from bring - ${this.status} - $upstreamBody" }

            SpoolmanCallFailed(ErrorResponse(this.status, upstreamBody))
        }

        return this
    }

    private fun String.color() = "#${this.uppercase()}"
}

fun Application.spoolmanService(client: HttpClient) =
    SpoolmanService(
        confStr("spoolman.url"),
        client,
    )
