package net.chrissearle.spoolman.scan

import kotlinx.serialization.Serializable

@Serializable
data class ScanPair(
    val spool: ScanID? = null,
    val location: ScanLocation? = null,
)
