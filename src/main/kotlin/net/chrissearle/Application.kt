package net.chrissearle

import io.ktor.server.application.Application
import io.ktor.server.cio.EngineMain

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module() {
    configureSerialization()
    configureMonitoring()
    configureRouting()
}
