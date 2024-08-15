/*
 * SPDX-FileCopyrightText: 2021 Atos, 2024 Lifely
 * SPDX-License-Identifier: EUPL-1.2+
 */
package net.atos.zac.app.zaak.model

import jakarta.json.bind.annotation.JsonbProperty
import nl.lifely.zac.util.AllOpen
import nl.lifely.zac.util.NoArgConstructor
import java.util.UUID

@AllOpen
@NoArgConstructor
data class RestZaakAssignmentToLoggedInUserData(
    var zaakUUID: UUID,

    @field:JsonbProperty("reden")
    var reason: String? = null
)
