package net.chrissearle.spoolman.scan

import arrow.core.raise.either
import arrow.core.raise.ensureNotNull
import kotlinx.serialization.Serializable
import net.chrissearle.api.IdRequired
import net.chrissearle.api.NotNumeric

@Serializable
data class ScanID(
    val id: Int
) {
    companion object {
        operator fun invoke(id: String?) =
            either {
                val present = ensureNotNull(id?.takeIf { it.isNotBlank() }) { IdRequired }
                val parsed = ensureNotNull(present.toIntOrNull()) { NotNumeric("id", id) }
                ScanID(parsed)
            }
    }
}
