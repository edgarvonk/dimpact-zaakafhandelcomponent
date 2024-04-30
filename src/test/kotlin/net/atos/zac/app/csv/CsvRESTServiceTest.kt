package net.atos.zac.app.csv

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.checkUnnecessaryStub
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import jakarta.ws.rs.core.StreamingOutput
import net.atos.zac.app.zoeken.converter.RESTZoekParametersConverter
import net.atos.zac.app.zoeken.createRESTZoekParameters
import net.atos.zac.app.zoeken.createZoekParameters
import net.atos.zac.app.zoeken.createZoekResultaatForZaakZoekObjecten
import net.atos.zac.csv.CsvService
import net.atos.zac.zoeken.ZoekenService

class CsvRESTServiceTest : BehaviorSpec({
    val zoekenService = mockk<ZoekenService>()
    val restZoekParametersConverter = mockk<RESTZoekParametersConverter>()
    val csvService = mockk<CsvService>()
    val csvRESTService = CsvRESTService(
        zoekenService,
        restZoekParametersConverter,
        csvService
    )

    beforeEach {
        checkUnnecessaryStub()
    }

    beforeSpec {
        clearAllMocks()
    }

    Given("The CSV REST service") {
        val restZoekParameters = createRESTZoekParameters()
        val zoekParameters = createZoekParameters()
        val zoekResultaat = createZoekResultaatForZaakZoekObjecten()
        val csvStreamingOutput = mockk<StreamingOutput>()

        every { restZoekParametersConverter.convert(restZoekParameters) } returns zoekParameters
        every { zoekenService.zoek(zoekParameters) } returns zoekResultaat
        every { csvService.exportToCsv(zoekResultaat) } returns csvStreamingOutput

        When("the download CSV function is called") {
            val response = csvRESTService.downloadCSV(restZoekParameters)

            Then("a CSV with the search results is returned") {
                response.status shouldBe 200
                response.entity shouldBe csvStreamingOutput
            }
        }
    }
})
