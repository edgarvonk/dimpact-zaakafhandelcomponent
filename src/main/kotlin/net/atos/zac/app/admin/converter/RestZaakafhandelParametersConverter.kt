/*
 * SPDX-FileCopyrightText: 2021 Atos, 2024 Lifely
 * SPDX-License-Identifier: EUPL-1.2+
 */
package net.atos.zac.app.admin.converter

import jakarta.inject.Inject
import net.atos.client.zgw.ztc.ZtcClientService
import net.atos.zac.admin.ZaakafhandelParameterService
import net.atos.zac.admin.model.ZaakafhandelParameters
import net.atos.zac.app.admin.model.RestZaakafhandelParameters
import net.atos.zac.app.zaak.converter.RestResultaattypeConverter
import net.atos.zac.app.zaak.model.RESTZaakStatusmailOptie
import nl.lifely.zac.util.AllOpen
import nl.lifely.zac.util.NoArgConstructor

@AllOpen
@NoArgConstructor
class RestZaakafhandelParametersConverter @Inject constructor(
    val caseDefinitionConverter: RESTCaseDefinitionConverter,
    val resultaattypeConverter: RestResultaattypeConverter,
    val zaakbeeindigParameterConverter: RESTZaakbeeindigParameterConverter,
    val humanTaskParametersConverter: RESTHumanTaskParametersConverter,
    val ztcClientService: ZtcClientService,
    val zaakafhandelParameterService: ZaakafhandelParameterService
) {
    fun convertZaakafhandelParameters(
        zaakafhandelParameters: ZaakafhandelParameters,
        inclusiefRelaties: Boolean
    ): RestZaakafhandelParameters {
        val restZaakafhandelParameters = RestZaakafhandelParameters(
            id = zaakafhandelParameters.id,
            zaaktype = ztcClientService.readZaaktype(zaakafhandelParameters.zaakTypeUUID).let {
                RESTZaaktypeOverzichtConverter.convert(it)
            },
            defaultGroepId = zaakafhandelParameters.groepID,
            defaultBehandelaarId = zaakafhandelParameters.gebruikersnaamMedewerker,
            einddatumGeplandWaarschuwing = zaakafhandelParameters.einddatumGeplandWaarschuwing,
            uiterlijkeEinddatumAfdoeningWaarschuwing = zaakafhandelParameters
                .uiterlijkeEinddatumAfdoeningWaarschuwing,
            creatiedatum = zaakafhandelParameters.creatiedatum,
            valide = zaakafhandelParameters.isValide,
            caseDefinition = zaakafhandelParameters.caseDefinitionID?.let {
                caseDefinitionConverter.convertToRESTCaseDefinition(it, inclusiefRelaties)
            },
            intakeMail = zaakafhandelParameters.intakeMail?.let { RESTZaakStatusmailOptie.valueOf(it) },
            afrondenMail = zaakafhandelParameters.afrondenMail?.let { RESTZaakStatusmailOptie.valueOf(it) },
            productaanvraagtype = zaakafhandelParameters.productaanvraagtype,
            domein = zaakafhandelParameters.domein
        )
        restZaakafhandelParameters.caseDefinition?.takeIf { inclusiefRelaties }?.let { caseDefinition ->
            zaakafhandelParameters.nietOntvankelijkResultaattype?.let {
                ztcClientService.readResultaattype(it).let { resultaatType ->
                    restZaakafhandelParameters.zaakNietOntvankelijkResultaattype =
                        resultaattypeConverter.convertResultaattype(resultaatType)
                }
            }
            restZaakafhandelParameters.humanTaskParameters =
                humanTaskParametersConverter.convertHumanTaskParametersCollection(
                    zaakafhandelParameters.humanTaskParametersCollection,
                    caseDefinition.humanTaskDefinitions
                )
            restZaakafhandelParameters.userEventListenerParameters = RESTUserEventListenerParametersConverter
                .convertUserEventListenerParametersCollection(
                    zaakafhandelParameters.userEventListenerParametersCollection,
                    caseDefinition.userEventListenerDefinitions
                )
            restZaakafhandelParameters.zaakbeeindigParameters =
                zaakbeeindigParameterConverter.convertZaakbeeindigParameters(zaakafhandelParameters.zaakbeeindigParameters)
            restZaakafhandelParameters.mailtemplateKoppelingen = RESTMailtemplateKoppelingConverter.convert(
                zaakafhandelParameters.mailtemplateKoppelingen
            )
            restZaakafhandelParameters.zaakAfzenders = RESTZaakAfzenderConverter.convertZaakAfzenders(
                zaakafhandelParameters.zaakAfzenders
            )
        }
        return restZaakafhandelParameters
    }

    fun convertRESTZaakafhandelParameters(
        restZaakafhandelParameters: RestZaakafhandelParameters
    ): ZaakafhandelParameters =
        zaakafhandelParameterService.readZaakafhandelParameters(
            restZaakafhandelParameters.zaaktype.uuid
        ).apply {
            id = restZaakafhandelParameters.id
            zaakTypeUUID = restZaakafhandelParameters.zaaktype.uuid
            zaaktypeOmschrijving = restZaakafhandelParameters.zaaktype.omschrijving
            caseDefinitionID = restZaakafhandelParameters.caseDefinition!!.key
            groepID = restZaakafhandelParameters.defaultGroepId
            uiterlijkeEinddatumAfdoeningWaarschuwing = restZaakafhandelParameters.uiterlijkeEinddatumAfdoeningWaarschuwing
            nietOntvankelijkResultaattype = restZaakafhandelParameters.zaakNietOntvankelijkResultaattype!!.id
            intakeMail = restZaakafhandelParameters.intakeMail?.name
            afrondenMail = restZaakafhandelParameters.afrondenMail?.name
            productaanvraagtype = restZaakafhandelParameters.productaanvraagtype?.trim()
            domein = restZaakafhandelParameters.domein
            gebruikersnaamMedewerker = restZaakafhandelParameters.defaultBehandelaarId
            einddatumGeplandWaarschuwing = restZaakafhandelParameters.einddatumGeplandWaarschuwing
        }.also {
            it.setHumanTaskParametersCollection(
                humanTaskParametersConverter.convertRESTHumanTaskParameters(
                    restZaakafhandelParameters.humanTaskParameters
                )
            )
            it.setUserEventListenerParametersCollection(
                RESTUserEventListenerParametersConverter.convertRESTUserEventListenerParameters(
                    restZaakafhandelParameters.userEventListenerParameters
                )
            )
            it.setZaakbeeindigParameters(
                zaakbeeindigParameterConverter.convertRESTZaakbeeindigParameters(
                    restZaakafhandelParameters.zaakbeeindigParameters
                )
            )
            it.setMailtemplateKoppelingen(
                RESTMailtemplateKoppelingConverter.convertRESTmailtemplateKoppelingen(
                    restZaakafhandelParameters.mailtemplateKoppelingen
                )
            )
            it.setZaakAfzenders(
                RESTZaakAfzenderConverter.convertRESTZaakAfzenders(restZaakafhandelParameters.zaakAfzenders)
            )
        }
}
