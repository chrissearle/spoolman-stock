package net.chrissearle.spoolman.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SpoolWithFirstUsed(
    val id: Int,
    @SerialName("first_used")
    val firstUsed: String,
)
