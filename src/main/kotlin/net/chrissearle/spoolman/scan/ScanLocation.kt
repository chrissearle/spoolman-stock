package net.chrissearle.spoolman.scan

import arrow.core.raise.either
import arrow.core.raise.ensureNotNull
import kotlinx.serialization.Serializable
import net.chrissearle.api.LocationRequired

@Serializable
data class ScanLocation(
    val location: String
) {
    companion object {
        operator fun invoke(location: String?) =
            either {
                ScanLocation(ensureNotNull(location?.takeIf { it.isNotBlank() }) { LocationRequired })
            }
    }
}
