package net.chrissearle.spoolman.web

import kotlinx.html.DIV
import kotlinx.html.a
import kotlinx.html.classes
import kotlinx.html.div

fun DIV.spoolmanLinks() {
    div {
        classes = setOf("list-group", "list-group-horizontal")

        a {
            classes = setOf("list-group-item", "list-group-item-action")
            href = "/spools"
            target = "_blank"

            +"Spoolman Spools"
        }

        a {
            classes = setOf("list-group-item", "list-group-item-action")
            href = "/filaments"
            target = "_blank"

            +"Spoolman Filaments"
        }

        a {
            classes = setOf("list-group-item", "list-group-item-action")
            href = "/locations"
            target = "_blank"

            +"Spoolman Locations"
        }
    }
}
