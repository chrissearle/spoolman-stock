package net.chrissearle.spoolman.model

import kotlinx.serialization.Serializable

@Serializable
data class SpoolWithLocation(
    val id: Int,
    val location: String? = null,
)
