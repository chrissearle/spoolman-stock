package net.chrissearle.api

import arrow.core.raise.Raise
import arrow.core.raise.catch
import arrow.core.raise.context.raise
import arrow.core.raise.context.withError
import java.util.Properties

object BuildInfo {
    private const val FALLBACK_TAG = "development"

    context(_: Raise<ApiError>)
    fun imageTag(): String =
        withError(::VersionNotReadable) {
            catch({
                BuildInfo::class.java
                    .getResourceAsStream("/build-info.properties")
                    ?.use { Properties().apply { load(it) }.getProperty("image.tag") }
                    ?.trim()
                    ?.takeIf { it.isNotEmpty() }
                    ?: FALLBACK_TAG
            }) { raise(it) }
        }
}
