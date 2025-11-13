package net.chrissearle

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import net.chrissearle.spoolman.normalizeShopUrl

class NormalizedShopUrlTest :
    FunSpec({
        test("https://example.com/foo") {
            "https://example.com/foo".normalizeShopUrl() shouldBe "https://example.com/foo"
        }

        test("\"https://example.com/foo\"") {
            "\"https://example.com/foo\"".normalizeShopUrl() shouldBe "https://example.com/foo"
        }
    })
