package net.chrissearle.spoolman.web

import kotlinx.html.ButtonType
import kotlinx.html.DIV
import kotlinx.html.FormMethod
import kotlinx.html.HTML
import kotlinx.html.InputType
import kotlinx.html.a
import kotlinx.html.body
import kotlinx.html.button
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.form
import kotlinx.html.h1
import kotlinx.html.h5
import kotlinx.html.h6
import kotlinx.html.id
import kotlinx.html.input
import kotlinx.html.p
import kotlinx.html.span
import kotlinx.html.style
import net.chrissearle.spoolman.model.Spool

fun HTML.spoolsBody(spools: List<Spool>) {
    body {
        navbar()

        div {
            classes = setOf("container", "mt-4", "mb-4")

            h1 { +"Spools" }

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
                    )

                val sortedSpools = spools.sortedByLocation()

                locationLinks(sortedSpools.keys.toList())

                for (location in sortedSpools) {
                    div {
                        id = "location-${location.key}"
                        classes = setOf("w-100", "mb-2", "mt-4")
                        h5 {
                            +location.key
                        }
                    }

                    val sortedSpools = location.value.sortedBy { it.filamentName ?: "" }

                    for (item in sortedSpools) {
                        spoolItem(item)
                    }
                }
            }
        }

        bootstrapScript()
    }
}

private fun DIV.locationLinks(locations: List<String>) {
    div {
        classes = setOf("list-group", "list-group-horizontal")

        for (location in locations) {
            a {
                classes = setOf("list-group-item", "list-group-item-action")
                href = "#location-$location"

                +location
            }
        }
    }
}

private fun List<Spool>.sortedByLocation() =
    this
        .groupBy { it.location ?: "Unspecified Location" }
        .toSortedMap()

private fun DIV.spoolItem(item: Spool) {
    div {
        classes = setOf("card")
        style = "width: 32%;"
        id = "spool-${item.id}"

        div {
            classes = setOf("row", "g-0")

            div {
                classes = setOf("col-md-2")
                style = "background-color: #${item.filamentColor ?: "#00000000"};"
            }

            div {
                classes = setOf("col-md-10")

                div {
                    classes = setOf("card-body")
                    h6 {
                        classes = setOf("card-title")

                        span {
                            +("${item.id}: ")
                            +(item.filamentName ?: "Unnamed Spool")
                        }
                    }

                    p {
                        classes = setOf("card-subtitle", "mb-2", "text-body-secondary")

                        +"${item.filamentVendor ?: "Unknown Vendor"} - ${item.filamentMaterial ?: "Unknown Material"}"
                    }

                    weightForm(item)
                }
            }
        }
    }
}

private fun DIV.weightForm(item: Spool) {
    form {
        method = FormMethod.post
        action = "/stock/spool"

        input {
            type = InputType.hidden
            name = "id"
            value = item.id.toString()
        }

        div {
            classes = setOf("row")

            div {
                classes = setOf("col-sm-8")

                input {
                    type = InputType.number
                    name = "weight"
                    classes = setOf("form-control")
                    placeholder = "Weight Used (g)"
                }
            }

            div {
                classes = setOf("col-sm-4")

                button {
                    classes = setOf("btn", "btn-primary", "btn-sm", "form-control", "col-sm-2")
                    type = ButtonType.submit

                    +"Use"
                }
            }
        }
    }
}
