/*
 * SPDX-FileCopyrightText: 2024 Lifely
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.smartdocuments.rest

import nl.lifely.zac.util.AllOpen
import nl.lifely.zac.util.NoArgConstructor

@NoArgConstructor
@AllOpen
data class RESTSmartDocumentsTemplateGroup(
    var id: String,
    var name: String,
    var groups: Set<RESTSmartDocumentsTemplateGroup>?,
    var templates: Set<RESTSmartDocumentsTemplate>?,
)

fun Set<RESTSmartDocumentsTemplateGroup>.toStringRepresentation(): Set<String> {
    val result = mutableSetOf<String>()
    this.forEach { result.addAll(convertTemplateGroupToStringRepresentation(it, null)) }
    return result
}

private fun convertTemplateGroupToStringRepresentation(
    group: RESTSmartDocumentsTemplateGroup,
    parent: String?
): Set<String> =
    arrayOf(parent, "group.${group.id}.${group.name}").filterNotNull().joinToString(".").let { groupString ->
        mutableSetOf(groupString).apply {
            group.templates?.mapTo(this) { "$groupString.template.${it.id}.${it.name}" }
            group.groups?.map { addAll(convertTemplateGroupToStringRepresentation(it, groupString)) }
        }
    }
