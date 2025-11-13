plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlinter)
    alias(libs.plugins.detekt)
    alias(libs.plugins.versions)
    alias(libs.plugins.serialization)
    alias(libs.plugins.dependency.analysis)
    jacoco
}

group = "net.chrissearle"
version = "0.0.1"

application {
    mainClass.set("net.chrissearle.ApplicationKt")
}

kotlin {
    jvmToolchain(22)

    compilerOptions {
        freeCompilerArgs = listOf("-Xcontext-parameters", "-Xconsistent-data-class-copy-visibility")
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.bundles.ktor.server)
    implementation(libs.bundles.ktor.client)
    implementation(libs.bundles.serialization)
    implementation(libs.bundles.monitoring)
    implementation(libs.arrow.core)
    runtimeOnly(libs.logback.classic)

    testImplementation(libs.bundles.test)
    testRuntimeOnly(libs.kotlin.test.junit)
}

tasks.shadowJar {
    archiveFileName.set("stock.jar")
}

tasks.jar {
    enabled = false
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
}
