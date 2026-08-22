package net.chrissearle.spoolman

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import net.chrissearle.spoolman.model.Spool

private val json =
    Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

private fun spoolJson(price: String) =
    """{"id": 1, "archived": false, "price": $price, "filament.id": 2}"""

private fun priceOf(price: String) = json.decodeFromString<Spool>(spoolJson(price)).price

class SpoolPriceTest :
    FunSpec({

        context("Spool price parsing") {
            test("integer price") {
                priceOf("279") shouldBe 279.0
            }

            test("decimal price with dot") {
                priceOf("279.0") shouldBe 279.0
            }

            test("quoted price with comma separator") {
                priceOf("\"279,0\"") shouldBe 279.0
            }

            test("quoted price with dot separator") {
                priceOf("\"279.5\"") shouldBe 279.5
            }

            test("null price") {
                priceOf("null") shouldBe null
            }

            test("missing price") {
                json.decodeFromString<Spool>("""{"id": 1, "archived": false, "filament.id": 2}""").price shouldBe null
            }

            test("non numeric price fails with a readable message") {
                shouldThrow<SerializationException> { priceOf("\"free\"") }
            }
        }
    })
