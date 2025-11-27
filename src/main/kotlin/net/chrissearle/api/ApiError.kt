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

abstract class RequiredField(
    val fieldName: String,
) : ApiError(
        ErrorResponse(
            status = HttpStatusCode.BadRequest,
            message = "$fieldName required"
        )
    )

data object IdRequired : RequiredField(fieldName = "id")

data object LocationRequired : RequiredField(fieldName = "location")

data object WeightRequired : RequiredField(fieldName = "weight")

data class NotNumeric(
    val field: String,
    val value: String?
) : ApiError(ErrorResponse(status = HttpStatusCode.BadRequest, message = "$field was not numeric: $value"))

data class SpoolNotFound(
    val id: Int
) : ApiError(ErrorResponse(status = HttpStatusCode.NotFound, message = "Spool not found: $id"))

data class LocationNotFound(
    val location: String
) : ApiError(ErrorResponse(status = HttpStatusCode.NotFound, message = "Location not found: $location"))

data class VersionNotReadable(
    val e: Throwable
) : ApiError(
        ErrorResponse(
            status = HttpStatusCode.InternalServerError,
            message = "${e.message}"
        )
    )
