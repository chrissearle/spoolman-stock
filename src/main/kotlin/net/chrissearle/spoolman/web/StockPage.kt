package net.chrissearle.spoolman.web

import kotlinx.html.DIV
import kotlinx.html.HTML
import kotlinx.html.a
import kotlinx.html.body
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.h1
import kotlinx.html.h5
import kotlinx.html.h6
import kotlinx.html.p
import kotlinx.html.style
import net.chrissearle.spoolman.model.StockSummary

fun HTML.stockBody(stock: List<StockSummary>) {
    body {
        navbar()

        div {
            classes = setOf("container", "mt-4", "mb-4")

            h1 { +"Summary" }

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
