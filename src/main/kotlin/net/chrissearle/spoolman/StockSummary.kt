package net.chrissearle.spoolman

import kotlinx.serialization.Serializable

@Serializable
data class StockSummary(
    val shop: String,
    val stock: Int,
    val count: Int,
    val color: String,
) {
    @Suppress("JoinDeclarationAndAssignment")
    val requiredStock: Int

    init {
        requiredStock = if (count < stock) stock - count else 0
    }
}
