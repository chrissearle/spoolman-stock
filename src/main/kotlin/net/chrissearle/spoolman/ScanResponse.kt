package net.chrissearle.spoolman

import kotlinx.serialization.Serializable

@Serializable
data class ScanResponse(
    val spool: ScanID? = null,
    val location: ScanLocation? = null,
)
