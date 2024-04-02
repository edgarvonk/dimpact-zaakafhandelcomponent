/*
 * SPDX-FileCopyrightText: 2021 Atos, 2024 Lifely
 * SPDX-License-Identifier: EUPL-1.2+
 */
package net.atos.zac.app.zaken.model

import jakarta.validation.constraints.NotBlank
import nl.lifely.zac.util.AllOpen
import nl.lifely.zac.util.NoArgConstructor
import java.util.UUID

@NoArgConstructor
@AllOpen
data class RESTZakenVerdeelGegevens(
    var uuids: List<UUID>,

    var reden: String?,

    @field:NotBlank
    var groepId: String? = null,

    @field:NotBlank
    var behandelaarGebruikersnaam: String? = null,

    /**
     * Unique screen event resource ID which can be used
     * to track the progress of the 'assign zaken from list' asynchronous job
     * using web sockets.
     */
    var screenEventResourceId: String? = null
)
