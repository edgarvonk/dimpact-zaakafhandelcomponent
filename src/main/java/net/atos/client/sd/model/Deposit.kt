/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */
package net.atos.client.sd.model

import jakarta.json.bind.annotation.JsonbProperty

open class Deposit {
    @JsonbProperty("SmartDocument")
    var smartDocument: SmartDocument? = null
}
