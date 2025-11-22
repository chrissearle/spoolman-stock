package net.chrissearle.spoolman.model

import arrow.core.raise.either
import arrow.core.raise.ensure
import kotlinx.serialization.Serializable
import net.chrissearle.api.IdRequired
import net.chrissearle.api.NotNumeric

@Serializable
data class SpoolWeightUsed(
    val id: Int,
    val weight: Int,
) {
    companion object {
        operator fun invoke(
            id: String?,
            weight: String?
        ) = either {
            ensure(!id.isNullOrBlank()) { IdRequired }
            ensure(id.toIntOrNull() != null) { NotNumeric("id", id) }
            ensure(!weight.isNullOrBlank()) { IdRequired }
            ensure(weight.toIntOrNull() != null) { NotNumeric("weight", weight) }

            SpoolWeightUsed(id.toInt(), weight.toInt())
        }
    }
}
