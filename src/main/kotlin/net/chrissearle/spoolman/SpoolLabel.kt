package net.chrissearle.spoolman

import kotlinx.serialization.Serializable

@Serializable
data class SpoolLabel(
    val id: Int,
    val comment: String? = null,
    val name: String? = null,
    val material: String? = null,
    val vendor: String? = null,
)
