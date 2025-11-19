package net.chrissearle.spoolman

import arrow.core.raise.either
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.server.sessions.Sessions
import io.ktor.server.sessions.cookie
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import io.ktor.server.sessions.set
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
import kotlinx.html.link
import kotlinx.html.p
import kotlinx.html.script
import kotlinx.html.style
import kotlinx.html.title
import net.chrissearle.api.ApiError
import net.chrissearle.api.respond
import net.chrissearle.api.respondHtml
import net.chrissearle.logCall

private val logger = KotlinLogging.logger {}

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

            get {
                either {
                    service.stockSummaries()
                }.respondHtml { stock ->
                    attributes["data-bs-theme"] = "dark"

                    pageHead()
                    pageBody(stock)
                }
            }
        }
    }
}

private fun Route.apiRouting(service: SpoolmanService) {
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

private fun Route.scanRouting(service: SpoolmanService) {
    route("/scan") {
        get("/spool/{id}") {
            logger.logCall(call)

            either {
                val spool = service.getSpool(ScanID(call.parameters["id"]).bind())

                var ctx = call.sessions.get<ScanContext>().getOrNew()

                if (ctx.lastLocation != null) {
                    service.updateSpoolLocation(spool, ctx.lastLocation)
                }

                ctx = ctx.copy(lastSpool = spool)

                call.sessions.set(ctx)

                ScanResponse(
                    spool = ctx.lastSpool,
                    location = ctx.lastLocation
                )
            }.respond()
        }

        get("/location/{location}") {
            logger.logCall(call)

            either {
                val location = service.getLocation(ScanLocation(call.parameters["location"]).bind())

                var ctx = call.sessions.get<ScanContext>().getOrNew()

                ctx = ctx.copy(lastLocation = location)

                call.sessions.set(ctx)

                ScanResponse(
                    spool = ctx.lastSpool,
                    location = ctx.lastLocation
                )
            }.respond()
        }

        get("/clear") {
            logger.logCall(call)

            either<ApiError, ScanResponse> {
                call.sessions.set(ScanContext())

                logger.info { "Cleared scan context" }

                ScanResponse()
            }.respond()
        }
    }
}

private fun HTML.pageBody(stock: List<StockSummary>) {
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

        script {
            src = "https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.min.js"
        }
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
