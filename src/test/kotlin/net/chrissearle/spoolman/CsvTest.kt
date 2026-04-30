package net.chrissearle.spoolman

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import net.chrissearle.spoolman.model.LocationLabel
import net.chrissearle.spoolman.model.Spool

private fun ByteArray.asLines() = toString(Charsets.UTF_8).split("\r\n").filter { it.isNotEmpty() }

class CsvTest :
    FunSpec({

        context("LocationLabel.toCsv") {
            test("empty list produces only header row") {
                val result = emptyList<LocationLabel>().toCsv().asLines()

                result.size shouldBe 1
                result[0] shouldBe "\"Location\",\"Link\""
            }

            test("location without link produces empty link field") {
                val result = listOf(LocationLabel("Drawer A")).toCsv().asLines()

                result.size shouldBe 2
                result[1] shouldBe "\"Drawer A\",\"\""
            }

            test("location with link includes link value") {
                val result = listOf(LocationLabel("Drawer A", "https://example.com")).toCsv().asLines()

                result.size shouldBe 2
                result[1] shouldBe "\"Drawer A\",\"https://example.com\""
            }

            test("multiple locations produce multiple rows") {
                val labels =
                    listOf(
                        LocationLabel("Drawer A", "https://a.example.com"),
                        LocationLabel("Drawer B"),
                        LocationLabel("Shelf C", "https://c.example.com"),
                    )

                val result = labels.toCsv().asLines()

                result.size shouldBe 4
                result[0] shouldBe "\"Location\",\"Link\""
                result[1] shouldBe "\"Drawer A\",\"https://a.example.com\""
                result[2] shouldBe "\"Drawer B\",\"\""
                result[3] shouldBe "\"Shelf C\",\"https://c.example.com\""
            }

            test("location name containing quotes is escaped") {
                val result = listOf(LocationLabel("Say \"hello\"")).toCsv().asLines()

                result[1] shouldBe "\"Say \"\"hello\"\"\",\"\""
            }

            test("location name containing commas is quoted") {
                val result = listOf(LocationLabel("Drawer, Top")).toCsv().asLines()

                result[1] shouldBe "\"Drawer, Top\",\"\""
            }

            test("output uses CRLF line endings") {
                val raw = listOf(LocationLabel("A")).toCsv().toString(Charsets.UTF_8)

                raw.contains("\r\n") shouldBe true
                raw.contains("\n\n") shouldBe false
            }
        }

        context("Spool.toCsv") {
            val minimalSpool =
                Spool(
                    id = 1,
                    archived = false,
                    filamentId = 10,
                )

            val fullSpool =
                Spool(
                    id = 42,
                    archived = false,
                    filamentId = 7,
                    filamentName = "PLA Basic",
                    filamentMaterial = "PLA",
                    filamentVendor = "Bambu",
                )

            test("empty list produces only header row") {
                val result = emptyList<Spool>().toCsv("https://host/spool/").asLines()

                result.size shouldBe 1
                result[0] shouldBe "\"ID\",\"Name\",\"Material\",\"Vendor\",\"Link\""
            }

            test("spool with no optional fields produces empty name, material, vendor") {
                val result = listOf(minimalSpool).toCsv("https://host/spool/").asLines()

                result.size shouldBe 2
                result[1] shouldBe "\"1\",\"\",\"\",\"\",\"https://host/spool/1\""
            }

            test("spool with all fields populates every column") {
                val result = listOf(fullSpool).toCsv("https://host/spool/").asLines()

                result.size shouldBe 2
                result[1] shouldBe "\"42\",\"PLA Basic\",\"PLA\",\"Bambu\",\"https://host/spool/42\""
            }

            test("link column concatenates prefix and spool id") {
                val result = listOf(fullSpool).toCsv("spool://").asLines()

                result[1].endsWith("\"spool://42\"") shouldBe true
            }

            test("multiple spools produce multiple rows") {
                val spools = listOf(minimalSpool, fullSpool)

                val result = spools.toCsv("p/").asLines()

                result.size shouldBe 3
                result[0] shouldBe "\"ID\",\"Name\",\"Material\",\"Vendor\",\"Link\""
                result[1] shouldBe "\"1\",\"\",\"\",\"\",\"p/1\""
                result[2] shouldBe "\"42\",\"PLA Basic\",\"PLA\",\"Bambu\",\"p/42\""
            }

            test("filament name containing quotes is escaped") {
                val spool = fullSpool.copy(filamentName = "My \"Special\" PLA")

                val result = listOf(spool).toCsv("p/").asLines()

                result[1].contains("\"My \"\"Special\"\" PLA\"") shouldBe true
            }

            test("output uses CRLF line endings") {
                val raw = listOf(fullSpool).toCsv("p/").toString(Charsets.UTF_8)

                raw.contains("\r\n") shouldBe true
            }
        }
    })
