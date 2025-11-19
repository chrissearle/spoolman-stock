package net.chrissearle.spoolman

import kotlinx.serialization.Serializable
import kotlin.time.Duration.Companion.minutes

val CONTEXT_TTL_S = 5.minutes
val CONTEXT_TTL_MS = CONTEXT_TTL_S.inWholeMilliseconds

@Serializable
data class ScanContext(
    val lastSpool: ScanID? = null,
    val lastLocation: ScanLocation? = null,
    val lastUpdated: Long = System.currentTimeMillis()
)

fun ScanContext?.freshOrNull(): ScanContext? =
    this?.takeIf { System.currentTimeMillis() - it.lastUpdated < CONTEXT_TTL_MS }

fun ScanContext?.getOrNew(): ScanContext = this.freshOrNull() ?: ScanContext()
