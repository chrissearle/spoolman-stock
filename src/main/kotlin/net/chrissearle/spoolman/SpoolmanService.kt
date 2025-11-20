package net.chrissearle.spoolman

import arrow.core.raise.Raise
import arrow.core.raise.context.ensure
import io.github.oshai.kotlinlogging.KotlinLogging
import net.chrissearle.api.ApiError
import net.chrissearle.api.LocationNotFound
import net.chrissearle.spoolman.model.LocationLabel
import net.chrissearle.spoolman.model.Spool
import net.chrissearle.spoolman.model.SpoolLabel
import net.chrissearle.spoolman.model.SpoolWithFirstUsed
import net.chrissearle.spoolman.model.SpoolWithLocation
import net.chrissearle.spoolman.model.StockSummary
import net.chrissearle.spoolman.scan.ScanID
import net.chrissearle.spoolman.scan.ScanLocation

private val logger = KotlinLogging.logger {}

class SpoolmanService(
    val spoolmanApi: SpoolmanApi,
    val scanConfig: ScanConfig,
    val startLocations: List<String>,
) {
    context(raise: Raise<ApiError>)
    suspend fun stockSummaries(): List<StockSummary> {
        val spools = unarchivedSpools()

        return stockFilaments()
            .map { filament ->
                val matchingSpools = spools.filter { it.filamentId == filament.id }

                StockSummary(
                    shop = filament.shopUrl!!,
                    stock = filament.stock!!,
                    count = matchingSpools.size,
                    color = filament.color!!.color(),
                    unopened = matchingSpools.count { spool -> !spool.started() },
                    name = filament.name,
                    material = filament.material,
                    vendor = filament.vendor,
                )
            }.also { logger.info { "Successfully fetched ${it.count()} stock spools." } }
    }

    context(raise: Raise<ApiError>)
    suspend fun stockFilaments() =
        spoolmanApi
            .fetchFilaments()
            .filter { it.stock != null }
            .filter { it.stock!! > 0 }
            .filter { !it.shopUrl.isNullOrBlank() }
            .filter { it.color != null }
            .map { it.copy(shopUrl = it.shopUrl!!.normalizeShopUrl()) }
            .also { logger.info { "Successfully fetched ${it.count()} stock filaments." } }

    context(raise: Raise<ApiError>)
    suspend fun spoolLabels() = unarchivedSpools().map { it.toLabel(scanConfig.spoolPrefix) }

    context(raise: Raise<ApiError>)
    suspend fun locationLabels(includeClear: Boolean = false) =
        spoolmanApi
            .fetchLocations()
            .map { LocationLabel(it, "${scanConfig.locationPrefix}$it") }
            .let { labels -> if (includeClear) labels + LocationLabel("clear", scanConfig.clearUrl) else labels }
            .let {
                if (it.none { label -> label.location == "Ext" }) {
                    it + LocationLabel("Ext", "${scanConfig.locationPrefix}/Ext")
                } else {
                    it
                }
            }

    context(raise: Raise<ApiError>)
    suspend fun locationLabel(location: ScanLocation) =
        getLocation(location).let {
            if (it.location == "clear") {
                LocationLabel(it.location, scanConfig.clearUrl)
            } else {
                LocationLabel(it.location, "${scanConfig.locationPrefix}${it.location}")
            }
        }

    context(raise: Raise<ApiError>)
    private suspend fun unarchivedSpools() =
        spoolmanApi
            .fetchSpools()
            .filter { !it.archived }
            .also { logger.info { "Successfully fetched ${it.count()} non-archived spools." } }

    context(raise: Raise<ApiError>)
    suspend fun getSpool(spool: ScanID): ScanID {
        val spool = spoolmanApi.getSpool(spool.id)

        return ScanID(spool.id)
    }

    context(raise: Raise<ApiError>)
    suspend fun getLocation(location: ScanLocation): ScanLocation {
        if (location.location == "clear" || location.location == "Ext") {
            return location
        }

        val locations = spoolmanApi.fetchLocations()

        ensure(locations.contains(location.location)) { LocationNotFound(location.location) }

        return location
    }

    context(raise: Raise<ApiError>)
    suspend fun updateSpoolLocation(
        spool: ScanID,
        location: ScanLocation
    ): SpoolWithLocation {
        val updateLocation = spoolmanApi.updateLocation(spool.id, location.location)

        if (startLocations.contains(updateLocation.location) && updateLocation.firstUsed.isNullOrBlank()) {
            updateSpoolFirstUsed(updateLocation.id)
        }

        return SpoolWithLocation(
            id = updateLocation.id,
            location = updateLocation.location
        )
    }

    context(raise: Raise<ApiError>)
    suspend fun updateSpoolFirstUsed(spool: Int): SpoolWithFirstUsed = spoolmanApi.updateFirstUsed(spool)
}

private fun String.color() = "#${this.uppercase()}"

private fun Spool.toLabel(spoolPrefix: String) =
    SpoolLabel(
        id = this.id,
        comment = this.comment,
        name = this.filamentName,
        material = this.filamentMaterial,
        vendor = this.filamentVendor,
        viewLink = "$spoolPrefix${this.id}"
    )

fun spoolmanService(
    spoolmanApi: SpoolmanApi,
    scanConfig: ScanConfig,
    startLocations: List<String>,
) = SpoolmanService(
    spoolmanApi = spoolmanApi,
    scanConfig = scanConfig,
    startLocations = startLocations
)
