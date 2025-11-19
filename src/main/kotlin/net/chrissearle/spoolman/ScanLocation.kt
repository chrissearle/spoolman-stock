package net.chrissearle.spoolman

import arrow.core.raise.either
import arrow.core.raise.ensure
import kotlinx.serialization.Serializable
import net.chrissearle.api.LocationRequired

@Serializable
data class ScanLocation(
    val location: String
) {
    companion object {
        operator fun invoke(location: String?) =
            either {
                ensure(!location.isNullOrBlank()) { LocationRequired }
                ScanLocation(location)
            }
    }
}
