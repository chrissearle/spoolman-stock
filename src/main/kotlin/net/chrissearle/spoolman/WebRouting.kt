package net.chrissearle.spoolman

import arrow.core.raise.either
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.ContentDisposition
import io.ktor.http.ContentType
import io.ktor.http.Parameters
import io.ktor.server.request.receiveParameters
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import net.chrissearle.api.redirect
import net.chrissearle.api.respondBytes
import net.chrissearle.api.respondHtml
import net.chrissearle.logCall
import net.chrissearle.spoolman.model.SpoolWeightUsed
import net.chrissearle.spoolman.scan.ScanLocation
import net.chrissearle.spoolman.web.locationsBody
import net.chrissearle.spoolman.web.page
import net.chrissearle.spoolman.web.spoolsBody
import net.chrissearle.spoolman.web.stockBody
import qrcode.QRCode
import qrcode.color.Colors

private val logger = KotlinLogging.logger {}

fun Route.webRouting(service: SpoolmanService) {
    route("/qr") {
        get("/locations") {
            either {
                service.locationLabels(true)
            }.respondHtml(
                page { locations ->
                    locationsBody(locations)
                }
            )
        }

        get("/location/{location}") {
            logger.logCall(call)

            either {
                val location = service.getLocation(ScanLocation(call.parameters["location"]).bind())

                val label = service.locationLabel(location)

                (label.link ?: label.location).qrBytes()
            }.respondBytes(
                contentType = ContentType.Image.PNG,
                contentDisposition =
                    ContentDisposition.Attachment
                        .withParameter(
                            ContentDisposition.Parameters.FileName,
                            "location-${call.parameters["location"]}.png"
                        )
            )
        }
    }

    post("/spool") {
        logger.logCall(call)

        either {
            val formParameters: Parameters = call.receiveParameters()

            service.useSpoolWeight(SpoolWeightUsed(formParameters["id"], formParameters["weight"]).bind())
        }.redirect("/stock/spools")
    }

    get("/spools") {
        logger.logCall(call)

        either {
            service.unarchivedSpools()
        }.respondHtml(
            page { spools ->
                spoolsBody(spools)
            }
        )
    }

    get {
        logger.logCall(call)

        either {
            service.stockSummaries()
        }.respondHtml(
            page { stock ->
                stockBody(stock)
            }
        )
    }
}

private fun String.qrBytes() =
    QRCode
        .ofSquares()
        .withBackgroundColor(Colors.WHITE)
        .build(this)
        .render()
        .getBytes()
