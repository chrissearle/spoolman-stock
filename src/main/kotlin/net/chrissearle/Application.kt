package net.chrissearle

import io.ktor.server.application.Application
import io.ktor.server.cio.EngineMain
import net.chrissearle.spoolman.ApiConfig
import net.chrissearle.spoolman.configureSpoolmanRouting
import net.chrissearle.spoolman.spoolmanApi
import net.chrissearle.spoolman.spoolmanService

fun Application.confStr(path: String) = environment.config.property(path).getString()

fun Application.confList(path: String) = environment.config.property(path).getList()

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module() {
    val client = configureClient()
    val apiConfig = ApiConfig(confStr("spoolman.apiPrefix"))
    val spoolmanApi = spoolmanApi(apiConfig, client)

    val upstreamHealthCheck =
        UpstreamHealthCheck(
            client = client,
            url = apiConfig.exportSpools
        )

    configureSerialization()
    configureMonitoring(upstreamHealthCheck)

    configureSpoolmanRouting(
        spoolmanService(
            spoolmanApi = spoolmanApi,
            spoolPrefix = confStr("spoolman.spoolPrefix"),
            locationPrefix = confStr("spoolman.locationPrefix"),
            clearUrl = confStr("spoolman.clearUrl"),
            startLocations = confList("spoolman.startLocations")
        )
    )
}
