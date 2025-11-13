package net.chrissearle.api

import io.ktor.http.HttpStatusCode
import kotlinx.serialization.Serializable
import net.chrissearle.HttpStatusCodeSerializer

@Serializable
data class ErrorResponse(
    @Serializable(with = HttpStatusCodeSerializer::class)
    val status: HttpStatusCode,
    val message: String,
    val fieldValue: String? = null,
)

sealed class ApiError(
    protected open val errorResponse: ErrorResponse,
) {
    open fun messageMap(): Map<String, ErrorResponse> = mapOf("error" to errorResponse)

    fun status() = this.errorResponse.status
}

abstract class UpstreamError(
    open val upstream: ErrorResponse,
    val systemName: String,
) : ApiError(
        ErrorResponse(
            status = HttpStatusCode.InternalServerError,
            message = "call to $systemName failed",
        ),
    ) {
    override fun messageMap() =
        mapOf(
            "upstream" to upstream,
            "error" to errorResponse,
        )
}

data class SpoolmanCallFailed(
    override val upstream: ErrorResponse,
) : UpstreamError(
        upstream = upstream,
        systemName = "Spoolman",
    )
