package net.chrissearle

import arrow.core.raise.either
import com.sksamuel.cohort.Cohort
import com.sksamuel.cohort.HealthCheckRegistry
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.HttpHeaders
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.metrics.micrometer.MicrometerMetrics
import io.ktor.server.plugins.callid.CallId
import io.ktor.server.plugins.callid.callIdMdc
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.request.path
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics
import io.micrometer.core.instrument.binder.system.ProcessorMetrics
import io.micrometer.prometheusmetrics.PrometheusConfig
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry
import kotlinx.coroutines.Dispatchers
import net.chrissearle.api.BuildInfo
import net.chrissearle.api.respondPlainText
import org.slf4j.event.Level
import kotlin.time.Duration.Companion.seconds

private val logger = KotlinLogging.logger {}

fun Application.configureMonitoring(upstreamHealthCheck: UpstreamHealthCheck) {
    val appMicrometerRegistry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)

    install(MicrometerMetrics) {
        registry = appMicrometerRegistry

        meterBinders =
            listOf(
                JvmMemoryMetrics(),
                JvmGcMetrics(),
                ProcessorMetrics(),
            )
    }
    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/stock") }
        callIdMdc("call-id")
    }
    install(CallId) {
        header(HttpHeaders.XRequestId)
        verify { callId: String ->
            callId.isNotEmpty()
        }
    }

    val readinessChecks =
        HealthCheckRegistry(Dispatchers.IO) {
            register("upstream-api", upstreamHealthCheck, 3.seconds, 10.seconds)
        }
    install(Cohort) {
        healthcheck("/stock/readiness", readinessChecks)
    }

    routing {
        get("/stock/api/metrics") {
            logger.logCall(call)

            call.respond(appMicrometerRegistry.scrape())
        }

        get("/stock/version") {
            logger.logCall(call)

            either {
                BuildInfo.imageTag()
            }.respondPlainText()
        }
    }
}
