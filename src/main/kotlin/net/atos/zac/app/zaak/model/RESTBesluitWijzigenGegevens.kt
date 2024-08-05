/*
 * SPDX-FileCopyrightText: 2022 Atos, 2024 Lifely
 * SPDX-License-Identifier: EUPL-1.2+
 */
package net.atos.zac.app.zaak.model

import jakarta.validation.constraints.NotNull
import nl.lifely.zac.util.AllOpen
import nl.lifely.zac.util.NoArgConstructor
import java.time.LocalDate
import java.util.UUID

@AllOpen
@NoArgConstructor
data class RESTBesluitWijzigenGegevens(
    @field:NotNull
    var besluitUuid: UUID,

    @field:NotNull
    var resultaattypeUuid: UUID,

    var toelichting: String? = null,

    var ingangsdatum: LocalDate? = null,

    var vervaldatum: LocalDate? = null,

    var informatieobjecten: List<UUID>? = null,

    var reden: String? = null
)
