package net.chrissearle.spoolman.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UseSpoolFilament(
    @SerialName("use_weight")
    val weight: Int,
)
