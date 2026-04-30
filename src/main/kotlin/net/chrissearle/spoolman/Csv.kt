package net.chrissearle.spoolman

import net.chrissearle.spoolman.model.LocationLabel
import net.chrissearle.spoolman.model.Spool

private fun String.toCsv() = "\"${replace("\"", "\"\"")}\""

private fun List<String>.toCsv() = joinToString(",") { it.toCsv() } + "\r\n"

private fun List<List<String>>.toCsv(vararg titles: String) =
    this.map { row -> row.toCsv() }.let { rows ->
        buildString {
            append(titles.toList().toCsv())
            rows.forEach { row -> append(row) }
        }.toByteArray()
    }

fun List<LocationLabel>.toCsv() = this.map { row -> listOf(row.location, row.link ?: "") }.toCsv("Location", "Link")

fun List<Spool>.toCsv(spoolPrefix: String) =
    this
        .map {
            listOf(
                it.id.toString(),
                it.filamentName ?: "",
                it.filamentMaterial ?: "",
                it.filamentVendor ?: "",
                "$spoolPrefix${it.id}"
            )
        }.toCsv(
            "ID",
            "Name",
            "Material",
            "Vendor",
            "Link"
        )
