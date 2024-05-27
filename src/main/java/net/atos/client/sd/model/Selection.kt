/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */
package net.atos.client.sd.model

import jakarta.json.bind.annotation.JsonbProperty

class Selection {
    @JsonbProperty("TemplateGroup")
    var templateGroup: String? = null

    @JsonbProperty("Template")
    var template: String? = null
}