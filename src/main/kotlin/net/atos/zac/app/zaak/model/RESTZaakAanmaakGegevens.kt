/*
 * SPDX-FileCopyrightText: 2023 Atos, 2024 Lifely
 * SPDX-License-Identifier: EUPL-1.2+
 */
package net.atos.zac.app.zaak.model

import jakarta.validation.Valid
import net.atos.zac.app.bag.model.RESTBAGObject
import net.atos.zac.app.productaanvragen.model.RESTInboxProductaanvraag
import nl.lifely.zac.util.AllOpen
import nl.lifely.zac.util.NoArgConstructor

@NoArgConstructor
@AllOpen
data class RESTZaakAanmaakGegevens(
    @field:Valid
    var zaak: RestZaak,

    var inboxProductaanvraag: RESTInboxProductaanvraag? = null,

    var bagObjecten: List<RESTBAGObject>? = null
)
