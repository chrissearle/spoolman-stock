package net.chrissearle.spoolman.web

import kotlinx.html.DIV
import kotlinx.html.HTML
import kotlinx.html.a
import kotlinx.html.body
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.h1
import kotlinx.html.h5
import kotlinx.html.img
import kotlinx.html.style
import net.chrissearle.spoolman.model.LocationLabel

fun HTML.locationsBody(locations: List<LocationLabel>) {
    body {
        navbar()

        div {
            classes = setOf("container", "mt-4", "mb-4")

            h1 { +"Locations" }

            div {
                classes =
                    setOf(
                        "mb-3",
                        "fs-6",
                        "text-body-secondary"
                    )

                spoolmanLinks()
            }

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
