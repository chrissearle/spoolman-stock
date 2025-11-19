package net.chrissearle.spoolman

import arrow.core.raise.either
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import net.chrissearle.api.respond
import net.chrissearle.logCall

private val logger = KotlinLogging.logger {}

fun Route.apiRouting(service: SpoolmanService) {
    route("/api") {
        get("/spools") {
            logger.logCall(call)

            either {
                service.spoolLabels()
            }.respond()
        }

        get("/locations") {
            logger.logCall(call)

            either {
                service.locationLabels()
            }.respond()
        }

        get("/stock") {
            logger.logCall(call)

            either {
                service.stockSummaries()
            }.respond()
        }
    }
}
