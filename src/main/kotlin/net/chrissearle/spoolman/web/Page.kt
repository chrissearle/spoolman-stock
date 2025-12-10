package net.chrissearle.spoolman.web

import kotlinx.html.BODY
import kotlinx.html.DIV
import kotlinx.html.HTML
import kotlinx.html.body
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.h1
import kotlinx.html.head
import kotlinx.html.script
import kotlinx.html.style
import kotlinx.html.title
import kotlinx.html.unsafe

fun <A> page(block: BODY.(A) -> Unit): HTML.(A) -> Unit =
    {
        attributes["class"] = "dark"

        pageHead()

        body {
            classes =
                setOf(
                    "min-h-screen",
                    "bg-slate-50",
                    "text-slate-900",
                    "dark:bg-slate-900",
                    "dark:text-slate-100"
                )

            navbar()

            block(it)
        }
    }

val tailwindConfig =
    """
    tailwind.config = {
      darkMode: 'class',
      theme: {
        extend: {
          colors: {
            brand: '#22c55e',
          },
        },
      },
    }
    """.trimIndent()

val printStylesheet =
    """
    @media print {
      .locations-grid {
        grid-template-columns: repeat(2, minmax(0, 1fr));
        gap: 1.25rem;
      }

      .location-card {
        break-inside: avoid;
        page-break-inside: avoid;
      }

      .location-card img {
        width: 110px;
        height: 110px;
      }
    }
    """.trimIndent()

private fun HTML.pageHead() {
    head {
        title { +"Spoolman Stock" }

        script {
            src = "https://cdn.tailwindcss.com"
        }

        script {
            unsafe {
                +tailwindConfig
            }
        }

        style {
            unsafe {
                +printStylesheet
            }
        }
    }
}

fun BODY.page(
    title: String,
    block: DIV.() -> Unit
) {
    div {
        attributes["class"] = "mx-auto max-w-7xl px-4 sm:px-6 lg:px-8 py-8"

        h1 {
            attributes["class"] = "text-3xl font-semibold tracking-tight mb-6"
            +title
        }

        div {
            block()
        }
    }
}
