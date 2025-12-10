package net.chrissearle.spoolman.web

import kotlinx.html.BODY
import kotlinx.html.ButtonType
import kotlinx.html.DIV
import kotlinx.html.FormMethod
import kotlinx.html.InputType
import kotlinx.html.a
import kotlinx.html.button
import kotlinx.html.div
import kotlinx.html.form
import kotlinx.html.h2
import kotlinx.html.h6
import kotlinx.html.id
import kotlinx.html.input
import kotlinx.html.p
import kotlinx.html.style
import net.chrissearle.spoolman.model.Spool

fun BODY.spoolsBody(spools: List<Spool>) {
    page("Spools") {
        attributes["class"] = "space-y-8"

        val sortedSpools = spools.sortedByLocation()

        locationLinks(sortedSpools.keys.toList())

        for ((locationName, locationSpools) in sortedSpools) {
            div {
                id = "location-$locationName"
                attributes["class"] = "mt-6 space-y-3"

                h2 {
                    attributes["class"] = "text-lg font-semibold tracking-tight"
                    +locationName
                }

                div {
                    attributes["class"] = "grid gap-4 sm:grid-cols-2 xl:grid-cols-3"

                    val spoolsAtLocation = locationSpools.sortedBy { it.filamentName ?: "" }
                    for (item in spoolsAtLocation) {
                        spoolItem(item)
                    }
                }
            }
        }
    }
}

private fun DIV.locationLinks(locations: List<String>) {
    div {
        attributes["class"] = "flex flex-wrap gap-2"

        for (location in locations) {
            a(href = "#location-$location") {
                attributes["class"] =
                    """
                    inline-flex items-center
                    rounded-full border border-slate-700
                    bg-slate-800/60
                    px-3 py-1 text-xs font-medium
                    text-slate-100
                    hover:bg-sky-500 hover:border-sky-500 hover:text-white
                    """.trimIndent()

                +location
            }
        }
    }
}

private fun List<Spool>.sortedByLocation() = this.groupBy { it.location ?: "Unspecified Location" }.toSortedMap()

private fun DIV.spoolItem(item: Spool) {
    val color = item.filamentColor?.let { "#$it" } ?: "#000000"
    val name = item.filamentName ?: "Unnamed Spool"
    val vendor = item.filamentVendor ?: "Unknown Vendor"
    val material = item.filamentMaterial ?: "Unknown Material"

    div {
        attributes["class"] =
            """
            flex flex-col
            rounded-lg overflow-hidden
            border border-slate-800/60
            bg-slate-900/40
            shadow-sm
            """.trimIndent()
        id = "spool-${item.id}"

        div {
            attributes["class"] = "h-3 w-full"
            style = "background-color: $color;"
        }

        div {
            attributes["class"] = "px-4 py-3 space-y-2"

            h6 {
                attributes["class"] = "text-sm font-semibold"
                +"${item.id}: $name"
            }

            p {
                attributes["class"] = "text-xs text-slate-400"
                +"$vendor – $material"
            }

            weightForm(item)
        }
    }
}

private fun DIV.weightForm(item: Spool) {
    form {
        method = FormMethod.post
        action = "/stock/spool"
        attributes["class"] =
            """
            mt-2
            space-y-2 sm:space-y-0
            sm:flex sm:items-center sm:gap-3
            """.trimIndent()

        input {
            type = InputType.hidden
            name = "id"
            value = item.id.toString()
        }

        div {
            attributes["class"] = "sm:flex-1"

            input {
                type = InputType.number
                name = "weight"
                attributes["class"] =
                    """
                    block w-full
                    rounded-md
                    border border-slate-700
                    bg-slate-900/60
                    px-3 py-1.5
                    text-xs text-slate-100
                    placeholder-slate-500
                    focus:outline-none focus:ring-2
                    focus:ring-sky-500 focus:border-sky-500
                    """.trimIndent()
                placeholder = "Weight used (g)"
                attributes["min"] = "0"
            }
        }

        div {
            attributes["class"] = "sm:w-auto"

            button {
                type = ButtonType.submit
                attributes["class"] =
                    """
                    mt-1 sm:mt-0
                    inline-flex w-full sm:w-auto
                    items-center justify-center
                    rounded-md
                    bg-sky-500 px-3 py-1.5
                    text-xs font-medium text-white
                    hover:bg-sky-400
                    focus:outline-none focus:ring-2 focus:ring-sky-500
                    """.trimIndent()

                +"Use"
            }
        }
    }
}
