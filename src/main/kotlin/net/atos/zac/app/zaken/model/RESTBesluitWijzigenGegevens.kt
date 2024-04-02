/*
 * SPDX-FileCopyrightText: 2022 Atos, 2024 Lifely
 * SPDX-License-Identifier: EUPL-1.2+
 */
package net.atos.zac.app.zaken.model

import nl.lifely.zac.util.AllOpen
import nl.lifely.zac.util.NoArgConstructor
import java.time.LocalDate
import java.util.UUID

@AllOpen
@NoArgConstructor
data class RESTBesluitWijzigenGegevens(
    var besluitUuid: UUID? = null,

    var resultaattypeUuid: UUID? = null,

    var toelichting: String? = null,

    var ingangsdatum: LocalDate? = null,

    var vervaldatum: LocalDate? = null,

    var informatieobjecten: List<UUID>? = null,

    var reden: String? = null
)
