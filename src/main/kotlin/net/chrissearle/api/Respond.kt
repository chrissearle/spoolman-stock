package net.chrissearle.api

import arrow.core.Either
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.ContentDisposition
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.html.respondHtml
import io.ktor.server.response.respond
import io.ktor.server.response.respondBytes
import io.ktor.server.routing.RoutingContext
import kotlinx.html.HTML
import net.chrissearle.logApiError

private val logger = KotlinLogging.logger {}

context(context: RoutingContext)
suspend inline fun <reified A : Any> Either<ApiError, A>.respond(status: HttpStatusCode = HttpStatusCode.OK) =
    when (this) {
        is Either.Left -> context.respond(value)
        is Either.Right -> context.call.respond(status, value)
    }

suspend fun RoutingContext.respond(error: ApiError) =
    call.respond(error.status(), error.messageMap()).also { logger.logApiError(error) }

context(context: RoutingContext)
suspend inline fun <reified A : Any> Either<ApiError, A>.respondHtml(noinline block: HTML.(A) -> Unit) {
    when (this) {
        is Either.Left -> context.respond(this.value) // your existing error handler
        is Either.Right -> {
            val payload = this.value

            context.call.respondHtml {
                block(payload)
            }
        }
    }
}

context(context: RoutingContext)
suspend fun Either<ApiError, ByteArray>.respondBytes(
    contentType: ContentType,
    contentDisposition: ContentDisposition? = null,
    status: HttpStatusCode = HttpStatusCode.OK,
) {
    when (this) {
        is Either.Left -> {
            context.respond(this.value)
        }

        is Either.Right -> {
            contentDisposition?.let {
                context.call.response.headers.append(
                    HttpHeaders.ContentDisposition,
                    it.toString()
                )
            }

            context.call.respondBytes(
                bytes = this.value,
                contentType = contentType,
                status = status
            )
        }
    }
}
