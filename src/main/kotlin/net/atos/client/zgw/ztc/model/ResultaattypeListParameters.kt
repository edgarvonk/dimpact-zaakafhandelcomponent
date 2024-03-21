/*
 * SPDX-FileCopyrightText: 2021 Atos, 2024 Lifely
 * SPDX-License-Identifier: EUPL-1.2+
 */
package net.atos.client.zgw.ztc.model

import jakarta.ws.rs.QueryParam
import java.net.URI

class ResultaattypeListParameters(
    private val zaaktype: URI
) : AbstractZTCListParameters() {
    /**
     * URL-referentie naar het ZAAKTYPE van ZAAKen waarin resultaten van dit RESULTAATTYPE bereikt kunnen worden.
     */
    @QueryParam("zaaktype")
    fun getZaaktype() = zaaktype
}
