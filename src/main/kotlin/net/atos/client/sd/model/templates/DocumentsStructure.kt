/*
 * SPDX-FileCopyrightText: 2024 Lifely
 * SPDX-License-Identifier: EUPL-1.2+
 */
package net.atos.client.sd.model.templates

import nl.lifely.zac.util.NoArgConstructor

@NoArgConstructor
data class DocumentsStructure(
    var templatesStructure: TemplatesStructure,
    var headersStructure: HeadersStructure
)
