package net.chrissearle.spoolman

import arrow.core.raise.Raise
import arrow.core.raise.context.ensure
import io.github.oshai.kotlinlogging.KotlinLogging
import net.chrissearle.api.ApiError
import net.chrissearle.api.LocationNotFound
import net.chrissearle.spoolman.model.LocationLabel
import net.chrissearle.spoolman.model.Spool
import net.chrissearle.spoolman.model.SpoolLabel
import net.chrissearle.spoolman.model.SpoolWeightUsed
import net.chrissearle.spoolman.model.SpoolWithFirstUsed
import net.chrissearle.spoolman.model.SpoolWithLocation
import net.chrissearle.spoolman.model.StockSummary
import net.chrissearle.spoolman.model.toStockFilament
import net.chrissearle.spoolman.scan.ScanID
import net.chrissearle.spoolman.scan.ScanLocation
import net.chrissearle.telemetry.raisingSpan

private val logger = KotlinLogging.logger {}

@Suppress("TooManyFunctions")
class SpoolmanService(
    val spoolmanApi: SpoolmanApi,
    val scanConfig: ScanConfig,
    val startLocations: List<String>,
) {
    context(_: Raise<ApiError>)
    suspend fun stockSummaries(): List<StockSummary> =
        raisingSpan("SpoolmanService.stockSummaries") { span ->
            val spools = unarchivedSpools()
            val filaments = stockFilaments()

            span.setAttribute("app.stock.spools.count", spools.size.toLong())
            span.setAttribute("app.stock.filaments.count", filaments.size.toLong())

            filaments
                .map { filament ->
                    val matchingSpools = spools.filter { it.filamentId == filament.id }

                    StockSummary(
                        shop = filament.shopUrl,
                        stock = filament.stock,
                        count = matchingSpools.size,
                        color = filament.color.color(),
                        unopened = matchingSpools.count { spool -> !spool.started() },
                        name = filament.name,
                        material = filament.material,
                        vendor = filament.vendor,
                    )
                }.also { span.setAttribute("app.stock.summaries.count", it.size.toLong()) }
                .also { logger.info { "Successfully fetched ${it.count()} stock spools." } }
        }

    context(_: Raise<ApiError>)
    suspend fun stockFilaments() =
        spoolmanApi
            .fetchFilaments()
            .mapNotNull { it.toStockFilament() }
            .also { logger.info { "Successfully fetched ${it.count()} stock filaments." } }

    context(_: Raise<ApiError>)
    suspend fun spoolLabels() =
        raisingSpan("SpoolmanService.spoolLabels") { span ->
            unarchivedSpools()
                .map { it.toLabel(scanConfig.spoolPrefix) }
                .also { span.setAttribute("app.labels.count", it.size.toLong()) }
        }

    context(_: Raise<ApiError>)
    suspend fun locationLabels(includeClear: Boolean = false) =
        raisingSpan("SpoolmanService.locationLabels") { span ->
            span.setAttribute("app.labels.include_clear", includeClear)

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
                }.also { span.setAttribute("app.labels.count", it.size.toLong()) }
        }

    context(_: Raise<ApiError>)
    suspend fun locationLabel(location: ScanLocation) =
        getLocation(location).let {
            if (it.location == "clear") {
                LocationLabel(it.location, scanConfig.clearUrl)
            } else {
                LocationLabel(it.location, "${scanConfig.locationPrefix}${it.location}")
            }
        }

    context(_: Raise<ApiError>)
    suspend fun unarchivedSpools() =
        spoolmanApi
            .fetchSpools()
            .filter { !it.archived }
            .also { logger.info { "Successfully fetched ${it.count()} non-archived spools." } }

    context(_: Raise<ApiError>)
    suspend fun getSpool(scanID: ScanID): ScanID = scanID.also { spoolmanApi.getSpool(it.id) }

    context(_: Raise<ApiError>)
    suspend fun getLocation(location: ScanLocation): ScanLocation {
        if (location.location == "clear" || location.location == "Ext") {
            return location
        }

        val locations = spoolmanApi.fetchLocations()

        ensure(locations.contains(location.location)) { LocationNotFound(location.location) }

        return location
    }

    context(_: Raise<ApiError>)
    suspend fun updateSpoolLocation(
        spool: ScanID,
        location: ScanLocation
    ): SpoolWithLocation =
        raisingSpan("SpoolmanService.updateSpoolLocation") { span ->
            span.setAttribute("app.spool.id", spool.id.toLong())
            span.setAttribute("app.spool.location", location.location)

            val updateLocation = spoolmanApi.updateLocation(spool.id, location.location)

            val setFirstUsed =
                startLocations.contains(updateLocation.location) && updateLocation.firstUsed.isNullOrBlank()

            if (setFirstUsed) {
                updateSpoolFirstUsed(updateLocation.id)
            }

            span.setAttribute("app.spool.first_used.set", setFirstUsed)

            SpoolWithLocation(
                id = updateLocation.id,
                location = updateLocation.location
            )
        }

    context(_: Raise<ApiError>)
    suspend fun updateSpoolFirstUsed(spool: Int): SpoolWithFirstUsed = spoolmanApi.updateFirstUsed(spool)

    context(_: Raise<ApiError>)
    suspend fun useSpoolWeight(spoolWeightUsed: SpoolWeightUsed) {
        raisingSpan("SpoolmanService.useSpoolWeight") { span ->
            span.setAttribute("app.spool.id", spoolWeightUsed.id.toLong())
            span.setAttribute("app.spool.weight", spoolWeightUsed.weight.toLong())

            spoolmanApi.useFilament(spoolWeightUsed.id, spoolWeightUsed.weight)
        }
    }
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
