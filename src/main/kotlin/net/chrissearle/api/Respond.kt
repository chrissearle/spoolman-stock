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
import io.ktor.server.response.respondRedirect
import io.ktor.server.response.respondText
import io.ktor.server.routing.RoutingContext
import io.opentelemetry.api.trace.Span
import io.opentelemetry.api.trace.StatusCode
import kotlinx.html.HTML
import net.chrissearle.logApiError

private val logger = KotlinLogging.logger {}

private const val SERVER_ERROR = 500

context(context: RoutingContext)
suspend inline fun <reified A : Any> Either<ApiError, A>.respond(status: HttpStatusCode = HttpStatusCode.OK) {
    onLeft { context.respond(it) }
    onRight { context.call.respond(status, it) }
}

context(context: RoutingContext)
suspend fun Either<ApiError, String>.redirect(permanent: Boolean = false) {
    onLeft { context.respond(it) }
    onRight { context.call.respondRedirect(it, permanent) }
}

private fun ApiError.recordOnSpan() {
    val span = Span.current()
    if (!span.spanContext.isValid) return

    span.setAttribute("app.error.type", this::class.simpleName ?: "ApiError")
    span.setAttribute("app.error.message", response.message)

    if (this is UpstreamError) {
        span.setAttribute("app.error.upstream.system", systemName)
        span.setAttribute("app.error.upstream.status", upstream.status.value.toLong())
        span.setAttribute("app.error.upstream.message", upstream.message)
    }

    if (this is VersionNotReadable) span.recordException(e)

    if (status().value >= SERVER_ERROR) {
        span.setStatus(StatusCode.ERROR, response.message)
    }
}

suspend fun RoutingContext.respond(error: ApiError) {
    error.recordOnSpan()

    call.respond(error.status(), error.messageMap()).also { logger.logApiError(error) }
}

context(context: RoutingContext)
suspend fun Either<ApiError, String>.respondPlainText(status: HttpStatusCode = HttpStatusCode.OK) {
    onLeft { context.respond(it) }
    onRight { context.call.respondText(status = status, text = it) }
}

context(context: RoutingContext)
suspend inline fun <reified A : Any> Either<ApiError, A>.respondHtml(noinline block: HTML.(A) -> Unit) {
    onLeft { context.respond(it) }
    onRight {
        context.call.respondHtml {
            block(it)
        }
    }
}

context(context: RoutingContext)
suspend fun Either<ApiError, ByteArray>.respondBytes(
    contentType: ContentType,
    contentDisposition: ContentDisposition? = null,
    status: HttpStatusCode = HttpStatusCode.OK,
) {
    onLeft { context.respond(it) }
    onRight {
        contentDisposition?.let { disposition ->
            context.call.response.headers.append(
                HttpHeaders.ContentDisposition,
                disposition.toString()
            )
        }

        context.call.respondBytes(
            bytes = it,
            contentType = contentType,
            status = status
        )
    }
}
