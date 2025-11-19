package net.chrissearle.spoolman

import kotlinx.serialization.Serializable

@Serializable
data class SpoolWithLocation(
    val id: Int,
    val location: String? = null,
)
