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
    val spoolsUrl: String,
    val filamentsUrl: String,
    val viewPrefix: String,
    val httpClient: HttpClient,
) {
    context(raise: Raise<ApiError>)
    suspend fun stockSummaries(): List<StockSummary> {
        val spools = unarchivedSpools()

        return stockFilaments()
            .map { filament ->
                val matchingSpools = spools.filter { it.filamentId == filament.id }

                StockSummary(
                    shop = filament.shopUrl!!,
                    stock = filament.stock!!,
                    count = matchingSpools.size,
                    color = filament.color!!.color(),
                    unopened = matchingSpools.count { spool -> !spool.started() },
                    name = filament.name,
                    material = filament.material,
                    vendor = filament.vendor,
                )
            }.also { logger.info { "Successfully fetched ${it.count()} stock spools." } }
    }

    context(raise: Raise<ApiError>)
    suspend fun stockFilaments() =
        fetchFilaments()
            .filter { it.stock != null }
            .filter { it.stock!! > 0 }
            .filter { !it.shopUrl.isNullOrBlank() }
            .filter { it.color != null }
            .map { it.copy(shopUrl = it.shopUrl!!.normalizeShopUrl()) }
            .also { logger.info { "Successfully fetched ${it.count()} stock filaments." } }

    context(raise: Raise<ApiError>)
    suspend fun spoolLabels() =
        unarchivedSpools()
            .map { it.toLabel(viewPrefix) }

    context(raise: Raise<ApiError>)
    private suspend fun unarchivedSpools() =
        fetchSpools()
            .filter { !it.archived }
            .also { logger.info { "Successfully fetched ${it.count()} non-archived spools." } }

    context(raise: Raise<ApiError>)
    suspend fun fetchSpools() =
        fetch<Spool>(spoolsUrl)
            .also { logger.info { "Successfully fetched ${it.count()} spools." } }

    context(raise: Raise<ApiError>)
    suspend fun fetchFilaments() =
        fetch<Filament>(filamentsUrl)
            .also { logger.info { "Successfully fetched ${it.count()} filaments." } }

    context(raise: Raise<ApiError>)
    suspend inline fun <reified T> fetch(url: String) =
        httpClient
            .request(url) {
                headers {
                    append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    append(HttpHeaders.Accept, ContentType.Application.Json.toString())
                }
            }.valid()
            .body<List<T>>()

    context(raise: Raise<ApiError>)
    suspend fun HttpResponse.valid(): HttpResponse {
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
    viewPrefix: String,
    filaments: String
) = SpoolmanService(
    spools,
    filaments,
    viewPrefix,
    client,
)
