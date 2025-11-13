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
    @SerialName("filament.color_hex")
    val filamentColor: String? = null,
    @SerialName("filament.extra.shop")
    val shopUrl: String? = null,
    @SerialName("filament.extra.stock")
    val stock: Int? = null,
    @SerialName("first_used")
    val firstUsed: String? = null,
) {
    fun started() = !firstUsed.isNullOrBlank()
}

fun String?.normalizeShopUrl(): String? {
    if (this == null) return null

    val s = this.trim()
    var result = s

    @Suppress("MagicNumber")
    when {
        // Case: \"https://example.com\"
        s.startsWith("\\\"") && s.endsWith("\\\"") && s.length >= 4 ->
            result = s.substring(2, s.length - 2)

        // Case: "https://example.com"
        s.startsWith("\"") && s.endsWith("\"") && s.length >= 2 ->
            result = s.substring(1, s.length - 1)
    }

    return result
}
