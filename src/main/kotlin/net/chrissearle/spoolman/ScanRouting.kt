package net.chrissearle.spoolman

import arrow.core.raise.Raise
import arrow.core.raise.either
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingCall
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import io.ktor.server.sessions.set
import net.chrissearle.api.ApiError
import net.chrissearle.api.respond
import net.chrissearle.logCall
import net.chrissearle.spoolman.scan.ScanContext
import net.chrissearle.spoolman.scan.ScanID
import net.chrissearle.spoolman.scan.ScanLocation
import net.chrissearle.spoolman.scan.ScanPair
import net.chrissearle.spoolman.scan.getOrNew

private val logger = KotlinLogging.logger {}

fun Route.scanRouting(service: SpoolmanService) {
    route("/scan") {
        get("/spool/{id}") {
            logger.logCall(call)

            either {
                val spool = service.getSpool(ScanID(call.parameters["id"]).bind())

                handleScan(
                    call = call,
                    service = service,
                    spool = spool
                )

                call.getScanContext().toScanPair()
            }.respond()
        }

        get("/location/{location}") {
            logger.logCall(call)

            either {
                val location = service.getLocation(ScanLocation(call.parameters["location"]).bind())

                handleScan(
                    call = call,
                    service = service,
                    location = location
                )

                call.getScanContext().toScanPair()
            }.respond()
        }

        get("/clear") {
            logger.logCall(call)

            either<ApiError, ScanPair> {
                call.setScanContext(ScanContext())

                logger.info { "Cleared scan context" }

                call.getScanContext().toScanPair()
            }.respond()
        }
    }
}

private fun RoutingCall.getScanContext(): ScanContext = this.sessions.get<ScanContext>().getOrNew()

private fun RoutingCall.setScanContext(ctx: ScanContext) = this.sessions.set(ctx)

private fun ScanContext.toScanPair() =
    ScanPair(
        spool = this.lastSpool,
        location = this.lastLocation
    )

context(raise: Raise<ApiError>)
private suspend fun handleScan(
    call: RoutingCall,
    service: SpoolmanService,
    spool: ScanID? = null,
    location: ScanLocation? = null
) {
    var ctx = call.getScanContext()

    ctx =
        ctx.copy(
            lastSpool = spool ?: ctx.lastSpool,
            lastLocation = location ?: ctx.lastLocation
        )

    if (ctx.lastLocation != null && ctx.lastSpool != null) {
        service.updateSpoolLocation(ctx.lastSpool, ctx.lastLocation)
    }

    call.setScanContext(ctx)
}
