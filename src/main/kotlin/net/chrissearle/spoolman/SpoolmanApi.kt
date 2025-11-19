package net.chrissearle.spoolman

import arrow.core.raise.Raise
import arrow.core.raise.ensure
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.headers
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import net.chrissearle.api.ApiError
import net.chrissearle.api.ErrorResponse
import net.chrissearle.api.SpoolNotFound
import net.chrissearle.api.SpoolmanCallFailed
import net.chrissearle.spoolman.model.Filament
import net.chrissearle.spoolman.model.Spool
import net.chrissearle.spoolman.model.SpoolWithFirstUsed
import net.chrissearle.spoolman.model.SpoolWithLocation
import net.chrissearle.spoolman.model.SpoolWithLocationAndFirstUsed
import java.time.Instant
import java.time.temporal.ChronoUnit

private val logger = KotlinLogging.logger {}

class SpoolmanApi(
    val apiConfig: ApiConfig,
    val httpClient: HttpClient,
) {
    context(raise: Raise<ApiError>)
    suspend fun fetchSpools() =
        fetch<Spool>(apiConfig.exportSpools)
            .also { logger.info { "Successfully fetched ${it.count()} spools." } }

    context(raise: Raise<ApiError>)
    suspend fun fetchFilaments() =
        fetch<Filament>(apiConfig.exportFilaments)
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
    suspend fun HttpResponse.valid(notFoundError: ApiError? = null): HttpResponse {
        raise.ensure(this.status.isSuccess()) {
            val upstreamBody = this.body<String>()

            logger.warn { "Failed to fetch data from bring - ${this.status} - $upstreamBody" }

            if (this.status == HttpStatusCode.NotFound && notFoundError != null) {
                notFoundError
            } else {
                SpoolmanCallFailed(ErrorResponse(this.status, upstreamBody))
            }
        }

        return this
    }

    context(raise: Raise<ApiError>)
    suspend fun fetchLocations() =
        fetch<String>(apiConfig.locations)
            .also { logger.info { "Successfully fetched ${it.count()} locations." } }

    context(raise: Raise<ApiError>)
    suspend fun getSpool(id: Int) =
        httpClient
            .request(apiConfig.spoolPrefix + "$id") {
                headers {
                    append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    append(HttpHeaders.Accept, ContentType.Application.Json.toString())
                }
            }.valid(SpoolNotFound(id))
            .body<SpoolWithLocation>()

    context(raise: Raise<ApiError>)
    suspend fun updateLocation(
        spoolId: Int,
        location: String
    ) = httpClient
        .request(apiConfig.spoolPrefix + "$spoolId") {
            method = HttpMethod.Patch
            headers {
                append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                append(HttpHeaders.Accept, ContentType.Application.Json.toString())
            }
            setBody(SpoolWithLocation(spoolId, location))
        }.valid(SpoolNotFound(spoolId))
        .body<SpoolWithLocationAndFirstUsed>()

    context(raise: Raise<ApiError>)
    suspend fun updateFirstUsed(spoolId: Int) =
        httpClient
            .request(apiConfig.spoolPrefix + "$spoolId") {
                method = HttpMethod.Patch
                headers {
                    append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    append(HttpHeaders.Accept, ContentType.Application.Json.toString())
                }
                setBody(
                    SpoolWithFirstUsed(
                        spoolId,
                        Instant
                            .now()
                            .truncatedTo(ChronoUnit.SECONDS)
                            .toString()
                    )
                )
            }.valid(SpoolNotFound(spoolId))
            .body<SpoolWithFirstUsed>()
}

fun spoolmanApi(
    apiConfig: ApiConfig,
    httpClient: HttpClient,
) = SpoolmanApi(
    apiConfig = apiConfig,
    httpClient = httpClient,
)
