/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */
package net.atos.zac.app.taken.model

import net.atos.client.zgw.shared.util.HistorieUtil
import nl.lifely.zac.util.AllOpen
import nl.lifely.zac.util.NoArgConstructor
import java.time.LocalDate
import java.time.ZonedDateTime

@AllOpen
@NoArgConstructor
data class RESTTaakHistorieRegel(
    var attribuutLabel: String,
    var oudeWaarde: String? = null,
    var nieuweWaarde: String? = null,
    var toelichting: String? = null,
    var datumTijd: ZonedDateTime? = null
) {
    constructor(
        attribuutLabel: String,
        oudeWaarde: LocalDate?,
        nieuweWaarde: LocalDate?,
        toelichting: String?
    ) : this(
        attribuutLabel,
        HistorieUtil.toWaarde(oudeWaarde),
        HistorieUtil.toWaarde(nieuweWaarde),
        toelichting
    )
}
