package net.chrissearle.api

import arrow.core.getOrElse
import arrow.core.raise.Raise
import arrow.core.raise.either

object BuildInfo {
    context(raise: Raise<ApiError>)
    fun imageTag(): String =
        either<Throwable, String> {
            BuildInfo::class.java
                .getResourceAsStream("/image-tag.txt")
                ?.use { it.readBytes().decodeToString().trim() }
                ?: "development"
        }.getOrElse {
            raise.raise(VersionNotReadable(it))
        }
}
