/*
 * SPDX-FileCopyrightText: 2022 Atos, 2024 Lifely
 * SPDX-License-Identifier: EUPL-1.2+
 */
package net.atos.zac.app.zaken.model

import nl.lifely.zac.util.AllOpen
import nl.lifely.zac.util.NoArgConstructor
import java.util.UUID

@NoArgConstructor
@AllOpen
data class RESTZaakKoppelGegevens(
    var zaakUuid: UUID,

    var teKoppelenZaakUuid: UUID,

    var relatieType: RelatieType,

    var reverseRelatieType: RelatieType?
)
