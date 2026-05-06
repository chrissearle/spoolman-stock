package net.chrissearle.spoolman.model

import arrow.core.raise.either
import arrow.core.raise.ensureNotNull
import kotlinx.serialization.Serializable
import net.chrissearle.api.IdRequired
import net.chrissearle.api.NotNumeric
import net.chrissearle.api.WeightRequired

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
            val nonBlankId = ensureNotNull(id?.takeIf { it.isNotBlank() }) { IdRequired }
            val parsedId = ensureNotNull(nonBlankId.toIntOrNull()) { NotNumeric("id", id) }
            val nonBlankWeight = ensureNotNull(weight?.takeIf { it.isNotBlank() }) { WeightRequired }
            val parsedWeight = ensureNotNull(nonBlankWeight.toIntOrNull()) { NotNumeric("weight", weight) }
            SpoolWeightUsed(parsedId, parsedWeight)
        }
    }
}
