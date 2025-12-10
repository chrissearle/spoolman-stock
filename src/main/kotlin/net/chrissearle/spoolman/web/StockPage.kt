package net.chrissearle.spoolman.web

import kotlinx.html.BODY
import kotlinx.html.DIV
import kotlinx.html.HTML
import kotlinx.html.a
import kotlinx.html.body
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.h5
import kotlinx.html.h6
import kotlinx.html.p
import kotlinx.html.span
import kotlinx.html.style
import net.chrissearle.spoolman.model.StockSummary

fun BODY.stockBody(stock: List<StockSummary>) {
    page("Summary") {
        div {
            attributes["class"] = "grid gap-6 sm:grid-cols-2 xl:grid-cols-3"

            for (item in stock.sortedWith(
                compareByDescending<StockSummary> { it.requiredStock }.thenBy {
                    it.name ?: ""
                }
            )) {
                spoolItem(item)
            }
        }
    }
}

private fun DIV.spoolItem(item: StockSummary) {
    val name = item.name ?: "Unnamed filament"

    div {
        attributes["class"] =
            """
            flex flex-col rounded-lg overflow-hidden
            border border-slate-800/60
            bg-slate-900/40
            shadow-sm
            """.trimIndent()

        div {
            attributes["class"] = "h-4 w-full"
            style = "background-color: ${item.color};"
        }

        div {
            attributes["class"] = "flex-1 px-4 py-3 space-y-1"

            h5 {
                attributes["class"] = "text-base font-semibold"
                +name
            }

            h6 {
                attributes["class"] = "text-sm text-slate-400"
                +"${item.vendor} – ${item.material}"
            }

            p {
                attributes["class"] = "mt-2 text-sm text-slate-300"
                +"Target: ${item.stock} · Actual: ${item.count} · Unopened: ${item.unopened}"
            }
        }

        spoolItemFooter(item)
    }
}

private fun DIV.spoolItemFooter(item: StockSummary) {
    div {
        attributes["class"] = "px-4 py-3 border-t border-slate-800 flex items-center justify-between text-sm"

        span {
            val requiredClass =
                if (item.requiredStock > 0) {
                    """
                    inline-flex items-center rounded-full bg-rose-500/20
                    text-rose-300 px-3 py-1 text-xs font-medium
                    """.trimIndent()
                } else {
                    """
                    inline-flex items-center rounded-full bg-emerald-500/15
                    text-emerald-300 px-3 py-1 text-xs font-medium
                    """.trimIndent()
                }

            attributes["class"] = requiredClass
            +"Required: ${item.requiredStock}"
        }

        val buttonClasses =
            if (item.requiredStock > 0) {
                """
                inline-flex items-center justify-center
                rounded-md px-3 py-1.5 text-xs font-medium
                bg-sky-500 hover:bg-sky-400
                text-white
                focus:outline-none focus:ring-2 focus:ring-sky-500
                """.trimIndent()
            } else {
                """
                inline-flex items-center justify-center
                rounded-md px-3 py-1.5 text-xs font-medium
                bg-slate-700 hover:bg-slate-600
                text-slate-100
                focus:outline-none focus:ring-2 focus:ring-slate-500
                """.trimIndent()
            }

        a(href = item.shop) {
            attributes["class"] = buttonClasses
            +"Purchase"
        }
    }
}
