/*
 * SPDX-FileCopyrightText: 2022 Atos, 2024 Lifely
 * SPDX-License-Identifier: EUPL-1.2+
 */
package net.atos.zac.app.zaak.model

import net.atos.client.zgw.ztc.model.Afleidingswijze
import net.atos.client.zgw.ztc.model.generated.ResultaatType
import net.atos.zac.util.PeriodUtil
import net.atos.zac.util.UriUtil
import nl.lifely.zac.util.AllOpen
import nl.lifely.zac.util.NoArgConstructor
import java.time.Period
import java.util.Locale
import java.util.UUID

@AllOpen
@NoArgConstructor
data class RestResultaattype(
    var id: UUID,

    var naam: String? = null,

    var naamGeneriek: String? = null,

    var vervaldatumBesluitVerplicht: Boolean,

    var besluitVerplicht: Boolean,

    var toelichting: String? = null,

    var archiefNominatie: String? = null,

    var archiefTermijn: String? = null,

    var selectielijst: String? = null,
)

fun ResultaatType.toRestResultaatType() = RestResultaattype(
    id = UriUtil.uuidFromURI(this.url),
    naam = this.omschrijving,
    toelichting = this.toelichting,
    archiefNominatie = this.archiefnominatie.name,
    archiefTermijn = this.archiefactietermijn?.let {
        PeriodUtil.format(Period.parse(it))
    },
    besluitVerplicht = this.brondatumArchiefprocedure?.afleidingswijze?.let {
        Afleidingswijze.VERVALDATUM_BESLUIT.toValue() == it.name.uppercase(Locale.getDefault()) ||
            Afleidingswijze.INGANGSDATUM_BESLUIT.toValue() == it.name.uppercase(Locale.getDefault())
    } ?: false,
    naamGeneriek = this.omschrijvingGeneriek,
    // compare enum values and not the enums themselves because we have multiple functionally
    // identical enums in our Java client code generated by the OpenAPI Generator
    vervaldatumBesluitVerplicht = this.brondatumArchiefprocedure?.afleidingswijze?.let {
        Afleidingswijze.VERVALDATUM_BESLUIT.toValue() == it.name.uppercase(Locale.getDefault())
    } ?: false
)

fun List<ResultaatType>.toRestResultaatTypes(): List<RestResultaattype> = this.map { it.toRestResultaatType() }