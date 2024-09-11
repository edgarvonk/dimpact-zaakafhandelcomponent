/*
 * SPDX-FileCopyrightText: 2024 Lifely
 * SPDX-License-Identifier: EUPL-1.2+
 */
package net.atos.client.smartdocuments.model.document

import jakarta.json.bind.annotation.JsonbDateFormat
import jakarta.json.bind.annotation.JsonbProperty
import net.atos.client.zgw.drc.model.generated.StatusEnum
import net.atos.zac.documentcreation.converter.DocumentCreationDataConverter.Companion.DATE_FORMAT
import java.net.URI
import java.time.LocalDate

data class AanvragerData(
    val naam: String? = null,
    val straat: String? = null,
    val huisnummer: String? = null,
    val postcode: String? = null,
    val woonplaats: String? = null
)

data class Data(
    @field:JsonbProperty("startformulier")
    val startformulierData: StartformulierData? = null,

    @field:JsonbProperty("zaak")
    val zaakData: ZaakData,

    @field:JsonbProperty("taak")
    val taakData: TaakData? = null,

    @field:JsonbProperty("gebruiker")
    val gebruikerData: GebruikerData,

    @field:JsonbProperty("aanvrager")
    val aanvragerData: AanvragerData? = null
)

data class Deposit(
    @field:JsonbProperty("SmartDocument")
    val smartDocument: SmartDocument,

    @Deprecated(
        "Only required for the SmartDocuments attended flow. " +
            "Will be removed in future once we no longer support the SmartDocuments attended flow."
    )
    @field:JsonbProperty("registratie")
    val registratie: Registratie? = null,

    @field:JsonbProperty("data")
    val data: Data? = null
)

data class GebruikerData(
    val id: String,
    val naam: String
)

data class OutputFormat(
    @field:JsonbProperty("OutputFormat")
    val outputFormat: String
)

@Deprecated(
    "Only required for the SmartDocuments attended flow. " +
        "Will be removed in future once we no longer support the SmartDocuments attended flow."
)
data class Registratie(
    @field:JsonbProperty("zaak")
    val zaak: URI,

    @field:JsonbProperty("informatieobjectStatus")
    val informatieObjectStatus: StatusEnum,

    @field:JsonbProperty("informatieobjecttype")
    val informatieObjectType: URI,

    @field:JsonbProperty("bronorganisatie")
    val bronOrganisatie: String,

    @field:JsonbProperty("creatiedatum")
    val creatieDatum: LocalDate,

    @field:JsonbProperty("auditToelichting")
    val auditToelichting: String
)

data class Selection(
    @field:JsonbProperty("TemplateGroup")
    val templateGroup: String? = null,

    @field:JsonbProperty("Template")
    val template: String? = null
)

data class SmartDocument(
    @field:JsonbProperty("Selection")
    val selection: Selection,

    @field:JsonbProperty("Variables")
    val variables: Variables? = null
)

data class StartformulierData(
    val productAanvraagtype: String,

    val data: Map<String, Any>
)

data class TaakData(
    val naam: String,
    var behandelaar: String? = null,
    val data: Map<String, Any>
)

data class Variables(
    @field:JsonbProperty("OutputFormats")
    val outputFormats: List<OutputFormat>
)

data class ZaakData(
    val zaaktype: String? = null,

    val identificatie: String? = null,

    val omschrijving: String? = null,

    val toelichting: String? = null,

    @field:JsonbDateFormat(DATE_FORMAT)
    val registratiedatum: LocalDate? = null,

    @field:JsonbDateFormat(DATE_FORMAT)
    val startdatum: LocalDate? = null,

    @field:JsonbDateFormat(DATE_FORMAT)
    val einddatumGepland: LocalDate? = null,

    @field:JsonbDateFormat(DATE_FORMAT)
    val uiterlijkeEinddatumAfdoening: LocalDate? = null,

    @field:JsonbDateFormat(DATE_FORMAT)
    val einddatum: LocalDate? = null,

    val communicatiekanaal: String? = null,

    val vertrouwelijkheidaanduiding: String? = null,

    val verlengingReden: String? = null,

    val opschortingReden: String? = null,

    val resultaat: String? = null,

    val status: String? = null,

    val besluit: String? = null,

    val groep: String? = null,

    val behandelaar: String? = null
)
