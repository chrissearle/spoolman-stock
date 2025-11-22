package net.chrissearle.spoolman.scan

import arrow.core.raise.either
import arrow.core.raise.ensure
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
                ensure(!id.isNullOrBlank()) { IdRequired }
                ensure(id.toIntOrNull() != null) { NotNumeric("id", id) }
                ScanID(id.toInt())
            }
    }
}
