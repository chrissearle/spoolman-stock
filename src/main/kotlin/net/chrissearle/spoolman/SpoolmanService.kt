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
import net.chrissearle.api.ApiError
import net.chrissearle.api.ErrorResponse
import net.chrissearle.api.SpoolmanCallFailed

private val logger = KotlinLogging.logger {}

class SpoolmanService(
    val apiUrl: String,
    val viewPrefix: String,
    val httpClient: HttpClient,
) {
    context(raise: Raise<ApiError>)
    suspend fun fetchStock() =
        fetchSpoolData()
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
    suspend fun fetchSpools() =
        fetchSpoolData()
            .map { it.toLabel(viewPrefix) }

    context(raise: Raise<ApiError>)
    private suspend fun fetchSpoolData() =
        fetch()
            .filter { !it.archived }

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

    private fun Spool.toLabel(viewPrefix: String) =
        SpoolLabel(
            id = this.id,
            comment = this.comment,
            name = this.filamentName,
            material = this.filamentMaterial,
            vendor = this.filamentVendor,
            viewLink = "$viewPrefix${this.id}"
        )
}

fun spoolmanService(
    client: HttpClient,
    spools: String,
    viewPrefix: String
) = SpoolmanService(
    spools,
    viewPrefix,
    client,
)
