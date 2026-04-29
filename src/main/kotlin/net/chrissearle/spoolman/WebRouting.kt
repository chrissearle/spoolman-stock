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
import net.chrissearle.spoolman.model.LocationLabel
import net.chrissearle.spoolman.model.Spool
import net.chrissearle.spoolman.model.SpoolWeightUsed
import net.chrissearle.spoolman.scan.ScanLocation
import net.chrissearle.spoolman.web.locationsBody
import net.chrissearle.spoolman.web.page
import net.chrissearle.spoolman.web.spoolsBody
import net.chrissearle.spoolman.web.stockBody
import qrcode.QRCode
import qrcode.color.Colors

private val logger = KotlinLogging.logger {}

private val CSV_CONTENT_TYPE = ContentType("text", "csv")

fun Route.webRouting(
    service: SpoolmanService,
    webConfig: WebConfig
) {
    val spoolmanHost = webConfig.spoolmanHost

    route("/qr") {
        qrRouting(service, spoolmanHost)
    }

    post("/spool") {
        logger.logCall(call)

        either {
            val formParameters: Parameters = call.receiveParameters()

            service.useSpoolWeight(SpoolWeightUsed(formParameters["id"], formParameters["weight"]).bind())
        }.redirect("/spools")
    }

    get("/spools") {
        logger.logCall(call)

        either {
            service.unarchivedSpools()
        }.respondHtml(
            page(spoolmanHost) { spools ->
                spoolsBody(spools)
            }
        )
    }

    get("/spools.csv") {
        logger.logCall(call)

        either {
            service.unarchivedSpools().toSpoolsCsv(service.scanConfig.spoolPrefix)
        }.respondBytes(
            contentType = CSV_CONTENT_TYPE,
            contentDisposition =
                ContentDisposition.Attachment
                    .withParameter(ContentDisposition.Parameters.FileName, "spools.csv")
        )
    }

    get {
        logger.logCall(call)

        either {
            service.stockSummaries()
        }.respondHtml(
            page(spoolmanHost) { stock ->
                stockBody(stock)
            }
        )
    }
}

private fun Route.qrRouting(
    service: SpoolmanService,
    spoolmanHost: String
) {
    get("/locations") {
        either {
            service.locationLabels(true)
        }.respondHtml(
            page(spoolmanHost) { locations ->
                locationsBody(locations)
            }
        )
    }

    get("/locations.csv") {
        logger.logCall(call)

        either {
            service.locationLabels().toLocationsCsv()
        }.respondBytes(
            contentType = CSV_CONTENT_TYPE,
            contentDisposition =
                ContentDisposition.Attachment
                    .withParameter(ContentDisposition.Parameters.FileName, "locations.csv")
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

private fun String.csvQuote() = "\"${replace("\"", "\"\"")}\""

private fun List<LocationLabel>.toLocationsCsv() =
    buildString {
        append("\"Location\",\"Link\"\r\n")
        for (loc in this@toLocationsCsv) {
            append("${loc.location.csvQuote()},${(loc.link ?: "").csvQuote()}\r\n")
        }
    }.toByteArray()

private fun List<Spool>.toSpoolsCsv(spoolPrefix: String) =
    buildString {
        append("\"ID\",\"Name\",\"Material\",\"Vendor\",\"Link\"\r\n")
        for (spool in this@toSpoolsCsv) {
            append("${spool.toCsvRow(spoolPrefix)}\r\n")
        }
    }.toByteArray()

private fun Spool.toCsvRow(spoolPrefix: String) =
    listOf(
        id.toString(),
        filamentName ?: "",
        filamentMaterial ?: "",
        filamentVendor ?: "",
        "$spoolPrefix$id",
    ).joinToString(",") { it.csvQuote() }

private fun String.qrBytes() =
    QRCode
        .ofSquares()
        .withBackgroundColor(Colors.WHITE)
        .build(this)
        .render()
        .getBytes()
