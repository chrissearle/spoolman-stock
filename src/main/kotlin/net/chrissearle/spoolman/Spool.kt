package net.chrissearle.spoolman

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
    @SerialName("filament.extra.shop")
    val shopUrl: String? = null,
    @SerialName("filament.extra.stock")
    val stock: Int? = null,
)

fun String?.normalizeShopUrl(): String? {
    if (this == null) return null

    // Trim whitespace first
    val s = this.trim()

    if (s.startsWith("\\\"") && s.endsWith("\\\"") && s.length >= 4) {
        return s.substring(2, s.length - 2)
    }

    if (s.startsWith("\"") && s.endsWith("\"") && s.length >= 2) {
        return s.substring(1, s.length - 1)
    }

    return s
}
