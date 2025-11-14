package net.chrissearle

import com.sksamuel.cohort.HealthCheck
import com.sksamuel.cohort.HealthCheckResult
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode

class UpstreamHealthCheck(
    private val client: HttpClient,
    private val url: String
) : HealthCheck {
    override suspend fun check(): HealthCheckResult =
        try {
            val response = client.get(url)
            if (response.status == HttpStatusCode.OK) {
                HealthCheckResult.healthy("Upstream $url responded with 200")
            } else {
                HealthCheckResult.unhealthy("Upstream $url returned ${response.status}")
            }
        } catch (t: Throwable) {
            HealthCheckResult.unhealthy("Error calling upstream $url", t)
        }
}
