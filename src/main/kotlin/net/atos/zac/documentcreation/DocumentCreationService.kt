/*
 * SPDX-FileCopyrightText: 2022 Atos, 2024 Lifely
 * SPDX-License-Identifier: EUPL-1.2+
 */
package net.atos.zac.documentcreation

import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.inject.Instance
import jakarta.inject.Inject
import net.atos.client.smartdocuments.model.document.Registratie
import net.atos.client.smartdocuments.model.document.Selection
import net.atos.client.smartdocuments.model.document.SmartDocument
import net.atos.client.zgw.drc.model.generated.StatusEnum
import net.atos.client.zgw.zrc.ZrcClientService
import net.atos.client.zgw.zrc.model.Zaak
import net.atos.client.zgw.ztc.ZtcClientService
import net.atos.client.zgw.ztc.model.generated.InformatieObjectType
import net.atos.zac.authentication.LoggedInUser
import net.atos.zac.configuratie.ConfiguratieService
import net.atos.zac.documentcreation.converter.DocumentCreationDataConverter
import net.atos.zac.documentcreation.model.DocumentCreationAttendedResponse
import net.atos.zac.documentcreation.model.DocumentCreationData
import net.atos.zac.documentcreation.model.DocumentCreationUnattendedResponse
import net.atos.zac.smartdocuments.SmartDocumentsService
import nl.lifely.zac.util.AllOpen
import nl.lifely.zac.util.NoArgConstructor
import java.time.LocalDate

@NoArgConstructor
@ApplicationScoped
@AllOpen
@Suppress("LongParameterList")
class DocumentCreationService @Inject constructor(
    private val smartDocumentsService: SmartDocumentsService,
    private val documentCreationDataConverter: DocumentCreationDataConverter,
    private val loggedInUserInstance: Instance<LoggedInUser>,
    private val ztcClientService: ZtcClientService,
    private val zrcClientService: ZrcClientService
) {
    companion object {
        private const val AUDIT_TOELICHTING = "Door SmartDocuments"
    }

    /**
     * Sends a request to SmartDocuments to create a document using the Smart Documents wizard (= attended mode).
     *
     * @param documentCreationData data used to create the document
     * @return the document creation response
     */
    fun createDocumentAttended(
        documentCreationData: DocumentCreationData
    ): DocumentCreationAttendedResponse =
        smartDocumentsService.createDocumentAttended(
            data = documentCreationDataConverter.createData(
                loggedInUser = loggedInUserInstance.get(),
                zaak = documentCreationData.zaak,
                taskId = documentCreationData.taskId
            ),
            registratie = createRegistratie(
                zaak = documentCreationData.zaak,
                informatieObjectType = documentCreationData.informatieobjecttype!!
            ),
            smartDocument = createSmartDocumentForAttendedFlow(documentCreationData)
        )

    /**
     * Sends a request to SmartDocuments to create a document using the Smart Documents unattended mode.
     *
     * @param documentCreationData data used to create the document
     * @return the document creation response
     */
    fun createDocumentUnattended(
        documentCreationData: DocumentCreationData
    ): DocumentCreationUnattendedResponse =
        smartDocumentsService.createDocumentUnattended(
            data = documentCreationDataConverter.createData(
                loggedInUser = loggedInUserInstance.get(),
                zaak = documentCreationData.zaak,
                taskId = documentCreationData.taskId
            ),
            smartDocument = createSmartDocumentForUnttendedFlow(documentCreationData)
        )

    /**
     * Creates a SmartDocument object for the attended flow.
     * In this flow the description of the zaaktype of the zaak in the provided document creation data is used
     * as the SmartDocuments template group.
     */
    private fun createSmartDocumentForAttendedFlow(documentCreationData: DocumentCreationData) =
        SmartDocument(
            selection = Selection(
                templateGroup = ztcClientService.readZaaktype(documentCreationData.zaak.zaaktype).omschrijving
            )
        )

    private fun createSmartDocumentForUnttendedFlow(documentCreationData: DocumentCreationData) =
        SmartDocument(
            selection = Selection(
                templateGroup = documentCreationData.templateGroupName,
                template = documentCreationData.templateName
            )
        )

    private fun createRegistratie(zaak: Zaak, informatieObjectType: InformatieObjectType) =
        Registratie(
            bronOrganisatie = ConfiguratieService.BRON_ORGANISATIE,
            zaak = zrcClientService.createUrlExternToZaak(zaak.uuid),
            informatieObjectStatus = StatusEnum.TER_VASTSTELLING,
            informatieObjectType = informatieObjectType.url,
            creatieDatum = LocalDate.now(),
            auditToelichting = AUDIT_TOELICHTING
        )
}