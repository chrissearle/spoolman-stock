package net.chrissearle.spoolman.web

import kotlinx.html.BODY
import kotlinx.html.ButtonType
import kotlinx.html.DIV
import kotlinx.html.NAV
import kotlinx.html.a
import kotlinx.html.button
import kotlinx.html.div
import kotlinx.html.id
import kotlinx.html.nav
import kotlinx.html.span

fun BODY.navbar() {
    nav {
        attributes["class"] =
            """
            bg-slate-900 border-b border-slate-800
            text-slate-100
            print:hidden
            """.trimIndent()

        div {
            attributes["class"] = "mx-auto max-w-7xl px-4 sm:px-6 lg:px-8"

            div {
                attributes["class"] = "flex h-16 items-center justify-between"

                div {
                    attributes["class"] = "flex items-center"

                    a(href = "/stock") {
                        attributes["class"] = "text-lg font-semibold tracking-tight"
                        +"Spoolman Stock"
                    }

                    div {
                        attributes["class"] = "hidden md:flex md:items-center md:space-x-4 ml-8"

                        a(href = "/stock") {
                            attributes["class"] = "text-sm font-medium hover:text-sky-400"
                            +"Stock Summary"
                        }

                        a(href = "/stock/qr/locations") {
                            attributes["class"] = "text-sm font-medium hover:text-sky-400"
                            +"Locations QR Codes"
                        }

                        a(href = "/stock/spools") {
                            attributes["class"] = "text-sm font-medium hover:text-sky-400"
                            +"Spools"
                        }

                        subMenu()
                    }
                }

                mobileMenuButton()
            }
        }

        mobileNav()
    }
}

private fun DIV.subMenu() {
    div {
        attributes["class"] = "relative"

        button {
            id = "spoolman-dropdown-button"
            type = ButtonType.button
            attributes["class"] =
                """
                inline-flex items-center justify-center
                rounded-md border border-slate-700
                px-3 py-1.5 text-sm font-medium
                bg-slate-800 hover:bg-slate-700
                focus:outline-none focus:ring-2 focus:ring-sky-500
                """.trimIndent()
            attributes["onclick"] =
                "document.getElementById('spoolman-dropdown').classList.toggle('hidden')"

            +"Spoolman"

            span {
                attributes["class"] = "ml-2 text-xs"
                +"▾"
            }
        }

        spoolmanSubMenu()
    }
}

private fun DIV.spoolmanSubMenu() {
    div {
        id = "spoolman-dropdown"
        attributes["class"] =
            """
            hidden absolute left-0 mt-2 w-56
            rounded-md shadow-lg
            bg-slate-800 ring-1 ring-black ring-opacity-5
            z-20
            """.trimIndent()

        div {
            attributes["class"] = "py-1"

            a(href = "/spool") {
                target = "_blank"
                attributes["class"] = "block px-4 py-2 text-sm hover:bg-slate-700"
                +"Spoolman Spools"
            }

            a(href = "/filament") {
                target = "_blank"
                attributes["class"] = "block px-4 py-2 text-sm hover:bg-slate-700"
                +"Spoolman Filaments"
            }

            a(href = "/locations") {
                target = "_blank"
                attributes["class"] = "block px-4 py-2 text-sm hover:bg-slate-700"
                +"Spoolman Locations"
            }
        }
    }
}

private fun DIV.mobileMenuButton() {
    div {
        attributes["class"] = "flex md:hidden"

        button {
            id = "mobile-menu-button"
            type = ButtonType.button
            attributes["class"] =
                """
                inline-flex items-center justify-center
                rounded-md p-2
                text-slate-200 hover:text-white
                hover:bg-slate-800
                focus:outline-none focus:ring-2 focus:ring-inset focus:ring-sky-500
                """.trimIndent()

            attributes["onclick"] = "document.getElementById('mobile-menu').classList.toggle('hidden')"

            span {
                attributes["class"] = "sr-only"
                +"Open main menu"
            }

            span {
                attributes["class"] = "block h-0.5 w-5 bg-slate-200 mb-1"
            }
            span {
                attributes["class"] = "block h-0.5 w-5 bg-slate-200 mb-1"
            }
            span {
                attributes["class"] = "block h-0.5 w-5 bg-slate-200"
            }
        }
    }
}

private fun NAV.mobileNav() {
    div {
        id = "mobile-menu"
        attributes["class"] = "md:hidden hidden border-t border-slate-800 bg-slate-900"

        div {
            attributes["class"] = "px-2 pt-2 pb-3 space-y-1"

            a(href = "/") {
                attributes["class"] = "block px-3 py-2 rounded-md text-base font-medium hover:bg-slate-800"
                +"Spoolman"
            }

            a(href = "/stock") {
                attributes["class"] = "block px-3 py-2 rounded-md text-base font-medium hover:bg-slate-800"
                +"Stock Summary"
            }

            a(href = "/stock/qr/locations") {
                attributes["class"] = "block px-3 py-2 rounded-md text-base font-medium hover:bg-slate-800"
                +"Locations QR Codes"
            }

            a(href = "/stock/spools") {
                attributes["class"] = "block px-3 py-2 rounded-md text-base font-medium hover:bg-slate-800"
                +"Spools"
            }
        }

        div {
            attributes["class"] = "border-t border-slate-800 px-2 pt-2 pb-3 space-y-1"

            span {
                attributes["class"] = "px-3 text-xs uppercase tracking-wide text-slate-400"
                +"Spoolman"
            }

            a(href = "/spool") {
                target = "_blank"
                attributes["class"] = "block px-3 py-2 rounded-md text-base font-medium hover:bg-slate-800"
                +"Spoolman Spools"
            }

            a(href = "/filament") {
                target = "_blank"
                attributes["class"] = "block px-3 py-2 rounded-md text-base font-medium hover:bg-slate-800"
                +"Spoolman Filaments"
            }

            a(href = "/locations") {
                target = "_blank"
                attributes["class"] = "block px-3 py-2 rounded-md text-base font-medium hover:bg-slate-800"
                +"Spoolman Locations"
            }
        }
    }
}
