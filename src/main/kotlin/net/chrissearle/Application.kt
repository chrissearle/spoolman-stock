package net.chrissearle

import io.ktor.server.application.Application
import io.ktor.server.cio.EngineMain
import net.chrissearle.spoolman.configureSpoolmanRouting
import net.chrissearle.spoolman.spoolmanService

fun Application.confStr(path: String) = environment.config.property(path).getString()

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module() {
    val client = configureClient()

    configureSerialization()
    configureMonitoring()
    configureRouting()

    configureSpoolmanRouting(spoolmanService(client))
}
