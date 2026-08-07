package net.chrissearle.telemetry

import arrow.core.raise.Raise
import arrow.core.raise.either
import io.opentelemetry.api.GlobalOpenTelemetry
import io.opentelemetry.api.trace.Span
import io.opentelemetry.api.trace.SpanKind
import io.opentelemetry.api.trace.StatusCode
import io.opentelemetry.api.trace.Tracer
import io.opentelemetry.extension.kotlin.asContextElement
import kotlinx.coroutines.withContext
import net.chrissearle.api.ApiError
import net.chrissearle.api.UpstreamError

const val INSTRUMENTATION_SCOPE = "net.chrissearle.spoolman-stock"

private val tracer: Tracer by lazy { GlobalOpenTelemetry.getTracer(INSTRUMENTATION_SCOPE) }

fun ApiError.applyTo(span: Span) {
    span.setStatus(StatusCode.ERROR, response.message)
    span.setAttribute("app.error.type", this::class.simpleName ?: "ApiError")
    span.setAttribute("app.error.message", response.message)
    if (this is UpstreamError) {
        span.setAttribute("app.error.upstream.system", systemName)
        span.setAttribute("app.error.upstream.status", upstream.status.value.toLong())
    }
}

@Suppress("TooGenericExceptionCaught")
suspend fun <T> span(
    name: String,
    block: suspend (Span) -> T,
): T {
    val span = tracer.spanBuilder(name).setSpanKind(SpanKind.INTERNAL).startSpan()

    return try {
        withContext(span.asContextElement()) { block(span) }
    } catch (t: Throwable) {
        span.setStatus(StatusCode.ERROR, t.message ?: t::class.simpleName.orEmpty())
        span.recordException(t)
        throw t
    } finally {
        span.end()
    }
}

context(raise: Raise<ApiError>)
suspend fun <T> raisingSpan(
    name: String,
    block: suspend Raise<ApiError>.(Span) -> T,
): T =
    raise.run {
        span(name) { current ->
            either { block(current) }.onLeft { error -> error.applyTo(current) }
        }.bind()
    }
