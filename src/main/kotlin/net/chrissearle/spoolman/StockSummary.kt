package net.chrissearle.spoolman

import kotlinx.serialization.Serializable

@Serializable
data class StockSummary(
    val shop: String,
    val stock: Int,
    val count: Int,
    val color: String,
    val unopened: Int,
) {
    @Suppress("JoinDeclarationAndAssignment")
    val requiredStock: Int

    init {
        requiredStock = if (unopened < stock) stock - unopened else 0
    }
}
