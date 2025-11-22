package net.chrissearle.spoolman.web

import kotlinx.html.BODY
import kotlinx.html.script

fun BODY.bootstrapScript() {
    script {
        src = "https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.min.js"
    }
}
