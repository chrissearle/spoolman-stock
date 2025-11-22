package net.chrissearle.spoolman.web

import kotlinx.html.BODY
import kotlinx.html.ButtonType
import kotlinx.html.a
import kotlinx.html.button
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.id
import kotlinx.html.li
import kotlinx.html.nav
import kotlinx.html.span
import kotlinx.html.ul

fun BODY.navbar() {
    nav {
        classes = setOf("navbar", "navbar-expand-lg")

        div {
            classes = setOf("container-fluid")

            a {
                classes = setOf("navbar-brand")
                href = "/stock"

                +"Spoolman Stock"
            }

            button {
                classes = setOf("navbar-toggler")
                type = ButtonType.button
                attributes["data-bs-toggle"] = "collapse"
                attributes["data-bs-target"] = "#navbarNav"
                attributes["aria-controls"] = "navbarNav"
                attributes["aria-expanded"] = "false"
                attributes["aria-label"] = "Toggle navigation"

                span {
                    classes = setOf("navbar-toggler-icon")
                }
            }

            div {
                classes = setOf("collapse", "navbar-collapse")
                id = "navbarNav"

                ul {
                    classes = setOf("navbar-nav")

                    li {
                        classes = setOf("nav-item")

                        a {
                            classes = setOf("nav-link")
                            href = "/"
                            target = "_blank"

                            +"Spoolman"
                        }
                    }

                    li {
                        classes = setOf("nav-item")

                        a {
                            classes = setOf("nav-link")
                            href = "/stock"

                            +"Stock Summary"
                        }
                    }

                    li {
                        classes = setOf("nav-item")

                        a {
                            classes = setOf("nav-link")
                            href = "/stock/qr/locations"

                            +"Locations QR Codes"
                        }
                    }
                }
            }
        }
    }
}
