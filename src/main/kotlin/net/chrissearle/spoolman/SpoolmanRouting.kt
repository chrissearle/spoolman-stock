package net.chrissearle.spoolman

import arrow.core.raise.either
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.server.application.Application
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import net.chrissearle.api.respond
import net.chrissearle.logCall

private val logger = KotlinLogging.logger {}

fun Application.configureSpoolmanRouting(service: SpoolmanService) {
    routing {
        get("/stock/api/stock") {
            logger.logCall(call)

            either {
                service.fetchStock()
            }.respond()
        }
    }
}
