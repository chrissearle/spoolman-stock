package net.chrissearle.spoolman.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Spool(
    val id: Int,
    val archived: Boolean,
    val location: String? = null,
    val comment: String? = null,
    val price: Int? = null,
    @SerialName("filament.id")
    val filamentId: Int,
    @SerialName("filament.name")
    val filamentName: String? = null,
    @SerialName("filament.material")
    val filamentMaterial: String? = null,
    @SerialName("filament.color_hex")
    val filamentColor: String? = null,
    @SerialName("filament.vendor.name")
    val filamentVendor: String? = null,
    @SerialName("filament.extra.shop")
    val shopUrl: String? = null,
    @SerialName("filament.extra.stock")
    val stock: Int? = null,
    @SerialName("first_used")
    val firstUsed: String? = null,
) {
    fun started() = !firstUsed.isNullOrBlank()
}
