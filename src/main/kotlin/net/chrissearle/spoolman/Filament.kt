package net.chrissearle.spoolman

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Filament(
    @SerialName("id")
    val id: Int,
    @SerialName("name")
    val name: String? = null,
    @SerialName("material")
    val material: String? = null,
    @SerialName("color_hex")
    val color: String? = null,
    @SerialName("vendor.name")
    val vendor: String? = null,
    @SerialName("extra.shop")
    val shopUrl: String? = null,
    @SerialName("extra.stock")
    val stock: Int? = null,
)
