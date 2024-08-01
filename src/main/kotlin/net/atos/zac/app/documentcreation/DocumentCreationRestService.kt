/*
 * SPDX-FileCopyrightText: 2024 Lifely
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.documentcreation

import jakarta.inject.Inject
import jakarta.inject.Singleton
import jakarta.validation.Valid
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import net.atos.client.zgw.zrc.ZrcClientService
import net.atos.client.zgw.ztc.ZtcClientService
import net.atos.zac.app.documentcreation.model.RestDocumentCreationAttendedData
import net.atos.zac.app.documentcreation.model.RestDocumentCreationResponse
import net.atos.zac.app.documentcreation.model.RestDocumentCreationUnattendedData
import net.atos.zac.app.util.exception.InputValidationFailedException
import net.atos.zac.configuratie.ConfiguratieService
import net.atos.zac.documentcreation.SmartDocumentsService
import net.atos.zac.documentcreation.model.DocumentCreationData
import net.atos.zac.policy.PolicyService
import net.atos.zac.policy.PolicyService.assertPolicy
import nl.lifely.zac.util.AllOpen
import nl.lifely.zac.util.NoArgConstructor

@Singleton
@Path("documentcreation")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@NoArgConstructor
@AllOpen
class DocumentCreationRestService @Inject constructor(
    private val policyService: PolicyService,
    private val smartDocumentsService: SmartDocumentsService,
    private val ztcClientService: ZtcClientService,
    private val zrcClientService: ZrcClientService
) {
    @POST
    @Path("/createdocumentattended")
    fun createDocumentAttended(
        @Valid restDocumentCreationAttendedData: RestDocumentCreationAttendedData
    ): RestDocumentCreationResponse {
        val zaak = zrcClientService.readZaak(restDocumentCreationAttendedData.zaakUUID).also {
            assertPolicy(policyService.readZaakRechten(it).creeerenDocument)
        }
        // documents created by SmartDocuments are always of the type 'bijlage'
        // the zaaktype of the specified zaak needs to be configured to be able to use this informatieObjectType
        return ztcClientService.readInformatieobjecttypen(zaak.zaaktype)
            .stream()
            .filter { ConfiguratieService.INFORMATIEOBJECTTYPE_OMSCHRIJVING_BIJLAGE == it.omschrijving }
            .findAny()
            .orElseThrow {
                InputValidationFailedException(
                    "No informatieobjecttype '${ConfiguratieService.INFORMATIEOBJECTTYPE_OMSCHRIJVING_BIJLAGE}' found for " +
                        "zaaktype '${zaak.zaaktype}'. Cannot create document."
                )
            }.let { informatieObjectType ->
                DocumentCreationData(
                    zaak,
                    restDocumentCreationAttendedData.taskId,
                    informatieObjectType
                ).let(smartDocumentsService::createDocumentAttended)
                    .let { RestDocumentCreationResponse(it.redirectUrl, it.message) }
            }
    }

    @POST
    @Path("/createdocumentunattended")
    fun createDocumentUnattended(
        @Valid restDocumentCreationUnattendedData: RestDocumentCreationUnattendedData
    ): RestDocumentCreationResponse {
        val zaak = zrcClientService.readZaak(restDocumentCreationUnattendedData.zaakUuid)
        assertPolicy(policyService.readZaakRechten(zaak).creeerenDocument)
        return DocumentCreationData(
            zaak = zaak,
            taskId = restDocumentCreationUnattendedData.taskId,
            templateGroup = restDocumentCreationUnattendedData.documentTemplateGroupName,
            template = restDocumentCreationUnattendedData.documentTemplateName,
        ).let(smartDocumentsService::createDocumentUnattended)
            .let { RestDocumentCreationResponse(message = it.message) }
    }
}
