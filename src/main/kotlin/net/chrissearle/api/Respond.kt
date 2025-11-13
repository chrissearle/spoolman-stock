package net.chrissearle.api

import arrow.core.Either
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.RoutingContext
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
