package net.chrissearle.spoolman

import arrow.core.raise.either
import arrow.core.raise.ensure
import kotlinx.serialization.Serializable
import net.chrissearle.api.IdNotNumeric
import net.chrissearle.api.IdRequired

@Serializable
data class ScanID(
    val id: Int
) {
    companion object {
        operator fun invoke(id: String?) =
            either {
                ensure(!id.isNullOrBlank()) { IdRequired }
                ensure(id.toIntOrNull() != null) { IdNotNumeric(id) }
                ScanID(id.toInt())
            }
    }
}
