package net.chrissearle.spoolman.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SpoolWithLocationAndFirstUsed(
    val id: Int,
    val location: String? = null,
    @SerialName("first_used")
    val firstUsed: String? = null,
)
