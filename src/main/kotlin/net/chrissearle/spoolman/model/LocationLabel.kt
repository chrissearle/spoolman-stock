package net.chrissearle.spoolman.model

import kotlinx.serialization.Serializable

@Serializable
data class LocationLabel(
    val location: String,
    val link: String? = null,
)
