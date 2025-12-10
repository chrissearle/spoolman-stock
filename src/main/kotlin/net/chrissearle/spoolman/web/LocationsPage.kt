package net.chrissearle.spoolman.web

import kotlinx.html.BODY
import kotlinx.html.DIV
import kotlinx.html.a
import kotlinx.html.div
import kotlinx.html.h5
import kotlinx.html.img
import net.chrissearle.spoolman.model.LocationLabel

fun BODY.locationsBody(locations: List<LocationLabel>) {
    page("Locations") {
        div {
            attributes["class"] = "grid gap-6 sm:grid-cols-2 xl:grid-cols-3 locations-grid"

            for (item in locations.sortedWith(
                compareBy { it.location }
            )) {
                locationItem(item)
            }
        }
    }
}

private fun DIV.locationItem(item: LocationLabel) {
    div {
        attributes["class"] =
            """
                location-card       
            flex flex-col items-center
            rounded-lg overflow-hidden
            border border-slate-800/60
            bg-slate-900/40
            shadow-sm
            px-4 py-5
            """.trimIndent()

        // QR image
        img {
            src = "/stock/qr/location/${item.location}"
            alt = "QR Code for ${item.location}"
            attributes["class"] = "mb-4"
            attributes["width"] = "150"
            attributes["height"] = "150"
        }

        // Location name
        h5 {
            attributes["class"] = "text-base font-semibold text-center"
            +item.location
        }

        if (item.link != null) {
            a(href = item.link) {
                attributes["class"] =
                    """
                    mt-4 inline-flex items-center justify-center
                    rounded-md px-3 py-1.5 text-xs font-medium
                    bg-sky-500 hover:bg-sky-400
                    text-white
                    focus:outline-none focus:ring-2 focus:ring-sky-500
                    print:hidden
                    """.trimIndent()

                +"Open location"
            }
        }
    }
}
