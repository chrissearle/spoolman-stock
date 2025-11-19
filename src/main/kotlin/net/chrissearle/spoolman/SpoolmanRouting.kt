package net.chrissearle.spoolman

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.server.sessions.Sessions
import io.ktor.server.sessions.cookie
import net.chrissearle.spoolman.scan.CONTEXT_TTL_S
import net.chrissearle.spoolman.scan.ScanContext

fun Application.configureSpoolmanRouting(service: SpoolmanService) {
    install(Sessions) {
        cookie<ScanContext>("scan_context") {
            cookie.path = "/stock/scan"
            cookie.maxAgeInSeconds = CONTEXT_TTL_S.inWholeSeconds
        }
    }

    routing {
        route("/stock") {
            apiRouting(service)

            scanRouting(service)

            webRouting(service)
        }
    }
}
