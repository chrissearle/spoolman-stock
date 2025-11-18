package net.chrissearle.spoolman

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
