package net.chrissearle

import io.github.oshai.kotlinlogging.KLogger
import io.ktor.server.request.uri
import io.ktor.server.routing.RoutingCall

fun KLogger.logCall(call: RoutingCall) {
    info { "Call: Request: ${call.request.uri}, Parameters: ${call.parameters}" }
}
