/*
 * SPDX-FileCopyrightText: 2022 Atos, 2024 Lifely
 * SPDX-License-Identifier: EUPL-1.2+
 */
package net.atos.zac.app.zaak.model

import jakarta.validation.constraints.NotBlank
import net.atos.zac.app.klant.model.klant.IdentificatieType
import nl.lifely.zac.util.AllOpen
import nl.lifely.zac.util.NoArgConstructor
import java.util.UUID

@AllOpen
@NoArgConstructor
data class RESTZaakBetrokkeneGegevens(
    var zaakUUID: UUID,

    var roltypeUUID: UUID,

    var roltoelichting: String?,

    var betrokkeneIdentificatieType: IdentificatieType,

    @field:NotBlank
    var betrokkeneIdentificatie: String
)
