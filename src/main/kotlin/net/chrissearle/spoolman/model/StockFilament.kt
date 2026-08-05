package net.chrissearle.spoolman.model

import arrow.core.raise.nullable
import net.chrissearle.spoolman.normalizeShopUrl

/**
 * A [Filament] that has been confirmed to be stocked - the fields the wire model leaves
 * optional are non-null here, so downstream code needs no null handling.
 */
data class StockFilament(
    val id: Int,
    val name: String? = null,
    val material: String? = null,
    val vendor: String? = null,
    val color: String,
    val shopUrl: String,
    val stock: Int,
)

fun Filament.toStockFilament(): StockFilament? =
    nullable {
        StockFilament(
            id = id,
            name = name,
            material = material,
            vendor = vendor,
            color = color.bind(),
            shopUrl = shopUrl?.takeIf { it.isNotBlank() }?.normalizeShopUrl().bind(),
            stock = stock?.takeIf { it > 0 }.bind(),
        )
    }
