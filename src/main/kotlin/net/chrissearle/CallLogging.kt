package net.chrissearle

import io.github.oshai.kotlinlogging.KLogger
import io.ktor.server.request.uri
import io.ktor.server.routing.RoutingCall
import net.chrissearle.api.ApiError

fun KLogger.logCall(call: RoutingCall) {
    info { "Call: Request: ${call.request.uri}, Parameters: ${call.parameters}" }
}

fun KLogger.logApiError(error: ApiError) {
    warn { "API Error: $error" }
}
