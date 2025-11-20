package net.chrissearle.spoolman

fun String?.normalizeShopUrl(): String? {
    if (this == null) return null

    val s = this.trim()
    var result = s

    @Suppress("MagicNumber")
    when {
        // Case: \"https://example.com\"
        s.startsWith("\\\"") && s.endsWith("\\\"") && s.length >= 4 -> {
            result = s.substring(2, s.length - 2)
        }

        // Case: "https://example.com"
        s.startsWith("\"") && s.endsWith("\"") && s.length >= 2 -> {
            result = s.substring(1, s.length - 1)
        }
    }

    return result
}

data class ApiConfig(
    val apiHost: String
) {
    val exportSpools = "$apiHost/api/v1/export/spools?fmt=json"
    val exportFilaments = "$apiHost/api/v1/export/filaments?fmt=json"
    val spoolPrefix = "$apiHost/api/v1/spool/"
    val locations = "$apiHost/api/v1/location"
}

data class ScanConfig(
    val scanHost: String
) {
    val locationPrefix = "$scanHost/stock/scan/location/"
    val spoolPrefix = "$scanHost/stock/scan/spool/"
    val clearUrl = "$scanHost/stock/scan/clear"
}
