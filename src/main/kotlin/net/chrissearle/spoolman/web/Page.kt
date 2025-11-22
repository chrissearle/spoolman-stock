package net.chrissearle.spoolman.web

import kotlinx.html.HTML
import kotlinx.html.head
import kotlinx.html.link
import kotlinx.html.title

fun <A> page(block: HTML.(A) -> Unit): HTML.(A) -> Unit =
    {
        attributes["data-bs-theme"] = "dark"
        pageHead()

        block(it)
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
