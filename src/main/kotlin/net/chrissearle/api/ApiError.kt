package net.chrissearle.api

import io.ktor.http.HttpStatusCode
import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    @Serializable(with = HttpStatusCodeSerializer::class)
    val status: HttpStatusCode,
    val message: String,
    val fieldValue: String? = null,
)

sealed interface ApiError {
    val response: ErrorResponse
}

fun ApiError.status() = response.status

fun ApiError.messageMap(): Map<String, ErrorResponse> =
    when (this) {
        is UpstreamError -> mapOf("upstream" to upstream, "error" to response)
        else -> mapOf("error" to response)
    }

abstract class UpstreamError(
    open val upstream: ErrorResponse,
    val systemName: String,
) : ApiError {
    override val response =
        ErrorResponse(
            status = HttpStatusCode.InternalServerError,
            message = "call to $systemName failed",
        )
}

data class SpoolmanCallFailed(
    override val upstream: ErrorResponse,
) : UpstreamError(
        upstream = upstream,
        systemName = "Spoolman",
    )

abstract class RequiredField(
    val fieldName: String,
) : ApiError {
    override val response =
        ErrorResponse(
            status = HttpStatusCode.BadRequest,
            message = "$fieldName required"
        )
}

data object IdRequired : RequiredField(fieldName = "id")

data object LocationRequired : RequiredField(fieldName = "location")

data object WeightRequired : RequiredField(fieldName = "weight")

data class NotNumeric(
    val field: String,
    val value: String?
) : ApiError {
    override val response =
        ErrorResponse(status = HttpStatusCode.BadRequest, message = "$field was not numeric: $value")
}

data class SpoolNotFound(
    val id: Int
) : ApiError {
    override val response =
        ErrorResponse(status = HttpStatusCode.NotFound, message = "Spool not found: $id")
}

data class LocationNotFound(
    val location: String
) : ApiError {
    override val response =
        ErrorResponse(status = HttpStatusCode.NotFound, message = "Location not found: $location")
}

data class VersionNotReadable(
    val e: Throwable
) : ApiError {
    override val response =
        ErrorResponse(
            status = HttpStatusCode.InternalServerError,
            message = "${e.message}"
        )
}
