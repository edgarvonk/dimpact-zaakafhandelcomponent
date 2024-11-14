/*
 * SPDX-FileCopyrightText: 2024 Lifely
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.smartdocuments

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.checkUnnecessaryStub
import io.mockk.every
import io.mockk.mockk
import jakarta.enterprise.inject.Instance
import net.atos.client.smartdocuments.SmartDocumentsClient
import net.atos.client.smartdocuments.model.createAttendedResponse
import net.atos.client.smartdocuments.model.createSmartDocument
import net.atos.client.smartdocuments.model.createsmartDocumentsTemplatesResponse
import net.atos.client.smartdocuments.model.document.OutputFormat
import net.atos.client.smartdocuments.model.document.Variables
import net.atos.client.smartdocuments.rest.DownloadedFile
import net.atos.zac.authentication.LoggedInUser
import net.atos.zac.authentication.createLoggedInUser
import net.atos.zac.documentcreation.model.createData
import nl.lifely.zac.util.toBase64String
import java.net.URI
import java.util.Optional

class SmartDocumentsServiceTest : BehaviorSpec({
    val smartDocumentsURL = "https://example.com/dummySmartDocumentsURL"
    val authenticationToken = "dummyAuthenticationToken"
    val fixedUserName = Optional.of("dummyFixedUserName")
    val loggedInUserInstance = mockk<Instance<LoggedInUser>>()
    val smartDocumentsClient = mockk<SmartDocumentsClient>()
    val smartDocumentsService = SmartDocumentsService(
        smartDocumentsClient = smartDocumentsClient,
        smartDocumentsURL = smartDocumentsURL,
        authenticationToken = authenticationToken,
        loggedInUserInstance = loggedInUserInstance,
        fixedUserName = fixedUserName
    )

    beforeEach {
        checkUnnecessaryStub()
    }

    Given("Document creation data with a zaak") {
        val loggedInUser = createLoggedInUser()
        val data = createData()
        val variables = Variables(
            outputFormats = listOf(OutputFormat("DOCX")),
            redirectMethod = "POST",
            redirectUrl = "url"
        )
        val smartDocument = createSmartDocument(variables)
        val attendedResponse = createAttendedResponse()
        every { loggedInUserInstance.get() } returns loggedInUser
        every { smartDocumentsClient.attendedDeposit(any(), any(), any()) } returns attendedResponse

        When("the 'create document attended' method is called") {
            val documentCreationResponse = smartDocumentsService.createDocumentAttended(
                data = data,
                smartDocument = smartDocument
            )

            Then(
                """
                the attended SmartDocuments document creation wizard is started and a document creation response is returned
                """
            ) {
                with(documentCreationResponse) {
                    redirectUrl shouldBe URI(
                        "$smartDocumentsURL/smartdocuments/wizard?ticket=${attendedResponse.ticket}"
                    )
                    message shouldBe null
                }
            }
        }
    }

    Given("Document is generated and ready for download") {
        val downloadedFile = mockk<DownloadedFile>()

        val fileName = "abcd.docx"
        val body = "body content".toByteArray(Charsets.UTF_8)

        every { smartDocumentsClient.downloadFile(any(), any()) } returns downloadedFile
        every { downloadedFile.body() } returns body
        every { downloadedFile.contentDisposition() } returns "attachment; filename=\"$fileName\""

        When("the 'download file' method is called") {
            val file = smartDocumentsService.downloadDocument("sdId")

            Then("a file object representing the content is returned") {
                with(file) {
                    fileName shouldBe fileName
                    outputFormat shouldBe "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                    document.data shouldBe body.toBase64String()
                }
            }
        }
    }

    Given("SmartDocuments contains templates") {
        val loggedInUser = createLoggedInUser()
        every { loggedInUserInstance.get() } returns loggedInUser

        val templatesResponse = createsmartDocumentsTemplatesResponse()
        every {
            smartDocumentsClient.listTemplates(any(), any())
        } returns templatesResponse

        When("list templates is called") {
            val templatesList = smartDocumentsService.listTemplates()

            Then("it should return a list of templates") {
                with(templatesList.documentsStructure.templatesStructure.templateGroups) {
                    size shouldBe 1
                    with(first()) {
                        name shouldBe "Dimpact"
                        templateGroups!!.size shouldBe 2
                        templateGroups!!.first().name shouldBe "Intern zaaktype voor test volledig gebruik ZAC"
                        templates!!.size shouldBe 2
                        templates!!.first().name shouldBe "Aanvullende informatie nieuw"
                    }
                }
            }
        }
    }
})
