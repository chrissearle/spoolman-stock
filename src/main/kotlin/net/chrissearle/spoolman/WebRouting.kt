package net.chrissearle.spoolman

import arrow.core.raise.either
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.ContentDisposition
import io.ktor.http.ContentType
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import kotlinx.html.BODY
import kotlinx.html.DIV
import kotlinx.html.HTML
import kotlinx.html.a
import kotlinx.html.body
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.h1
import kotlinx.html.h5
import kotlinx.html.h6
import kotlinx.html.head
import kotlinx.html.img
import kotlinx.html.link
import kotlinx.html.p
import kotlinx.html.script
import kotlinx.html.style
import kotlinx.html.title
import net.chrissearle.api.respondBytes
import net.chrissearle.api.respondHtml
import net.chrissearle.logCall
import net.chrissearle.spoolman.model.LocationLabel
import net.chrissearle.spoolman.model.StockSummary
import net.chrissearle.spoolman.scan.ScanLocation
import qrcode.QRCode
import qrcode.color.Colors

private val logger = KotlinLogging.logger {}

fun Route.webRouting(service: SpoolmanService) {
    route("/qr") {
        get("/locations") {
            either {
                service.locationLabels(true)
            }.respondHtml { locations ->
                attributes["data-bs-theme"] = "dark"

                pageHead()
                locationsBody(locations)
            }
        }

        get("/location/{location}") {
            logger.logCall(call)

            either {
                val location = service.getLocation(ScanLocation(call.parameters["location"]).bind())

                val label = service.locationLabel(location)

                QRCode
                    .ofSquares()
                    .withBackgroundColor(Colors.WHITE)
                    .build(label.link ?: label.location)
                    .render()
                    .getBytes()
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

    get {
        either {
            service.stockSummaries()
        }.respondHtml { stock ->
            attributes["data-bs-theme"] = "dark"

            pageHead()
            stockBody(stock)
        }
    }
}

private fun HTML.locationsBody(locations: List<LocationLabel>) {
    body {
        div {
            classes = setOf("container", "mt-4", "mb-4")

            h1 { +"Spoolman Locations" }

            div {
                classes =
                    setOf(
                        "d-flex",
                        "flex-wrap",
                        "gap-3",
                        "justify-content-center"
                    )

                for (item in locations.sortedWith(
                    compareBy { it.location }
                )) {
                    locationItem(item)
                }
            }
        }

        bootstrapScript()
    }
}

private fun HTML.stockBody(stock: List<StockSummary>) {
    body {
        div {
            classes = setOf("container", "mt-4", "mb-4")

            h1 { +"Spoolman Stock" }

            div {
                classes =
                    setOf(
                        "d-flex",
                        "flex-wrap",
                        "gap-3",
                        "justify-content-center"
                    )

                for (item in stock.sortedWith(
                    compareByDescending<StockSummary> { it.requiredStock }.thenBy { it.name }
                )) {
                    spoolItem(item)
                }
            }
        }

        bootstrapScript()
    }
}

private fun BODY.bootstrapScript() {
    script {
        src = "https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.min.js"
    }
}

private fun HTML.pageHead() {
    head {
        title { +"Spoolman Stock" }

        link {
            rel = "stylesheet"
            href = "https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
        }
    }
}

private fun DIV.spoolItem(item: StockSummary) {
    div {
        classes = setOf("card mb-4")
        style = "width: 30rem;"

        div {
            classes = setOf("row", "g-0")

            div {
                classes = setOf("col-md-2")
                style = "background-color: ${item.color};"
            }

            div {
                classes = setOf("col-md-10")

                div {
                    classes = setOf("card-body")
                    h5 {
                        classes = setOf("card-title")

                        +"${item.name}"
                    }

                    h6 {
                        classes = setOf("card-subtitle", "mb-2", "text-body-secondary")

                        +"${item.vendor} - ${item.material}"
                    }

                    p {
                        classes = setOf("text-body-secondary", "fs-6", "m-0", "p-0")

                        +"Target: ${item.stock} - Actual: ${item.count} - Unopened: ${item.unopened}"
                    }
                }
            }
            div {
                classes = setOf("text-body-secondary", "card-footer", "fs-6", "text-end")

                +"Required: ${item.requiredStock}"

                val buttonClass =
                    if (item.requiredStock > 0) {
                        "btn-primary"
                    } else {
                        "btn-secondary"
                    }

                a {
                    href = item.shop
                    classes = setOf("btn", buttonClass, "ms-3", "btn-sm")

                    +"Purchase"
                }
            }
        }
    }
}

private fun DIV.locationItem(item: LocationLabel) {
    div {
        classes = setOf("card mb-4")
        style = "width: 150em;"

        div {
            classes = setOf("row", "g-0")

            div {
                classes = setOf("col-md-2")

                img {
                    classes = setOf("img-fluid")
                    style = "width: 150px; height: 150px"
                    src = "/stock/qr/location/${item.location}"
                    alt = "QR Code for ${item.location}"
                }
            }

            div {
                classes = setOf("col-md-10")

                div {
                    classes = setOf("card-body")
                    h5 {
                        classes = setOf("card-title")

                        +item.location
                    }

                    if (item.link != null) {
                        a {
                            href = item.link
                            classes = setOf("ms-3")

                            +item.link
                        }
                    }
                }
            }
        }
    }
}
