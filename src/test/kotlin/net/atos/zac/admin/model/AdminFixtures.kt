/*
 * SPDX-FileCopyrightText: 2023 Lifely
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.admin.model

import java.time.ZonedDateTime
import java.util.UUID

fun createHumanTaskParameters(
    id: Long = 1234L,
    zaakafhandelParameters: ZaakafhandelParameters = createZaakafhandelParameters(),
    isActief: Boolean = true
) = HumanTaskParameters().apply {
    this.id = id
    this.zaakafhandelParameters = zaakafhandelParameters
    this.isActief = isActief
}

fun createHumanTaskReferentieTabel(
    id: Long = 1234L,
    referenceTable: ReferenceTable = createReferenceTable(),
    humanTaskParameters: HumanTaskParameters = createHumanTaskParameters(),
    field: String = "dummyField",
) = HumanTaskReferentieTabel().apply {
    this.id = id
    this.tabel = referenceTable
    this.humantask = humanTaskParameters
    this.veld = field
}

fun createReferenceTable(
    id: Long = 1234L,
    code: String = "dummyCode",
    name: String = "dummyReferentieTabel",
    isSystemReferenceTable: Boolean = false,
    values: MutableList<ReferenceTableValue> = mutableListOf(createReferenceTableValue())
) = ReferenceTable().apply {
    this.id = id
    this.code = code
    this.name = name
    this.isSystemReferenceTable = isSystemReferenceTable
    this.values = values
}

fun createReferenceTableValue(
    id: Long = 1234L,
    name: String = "dummyReferentieTabelWaarde",
    sortOrder: Int = 1,
    isSystemValue: Boolean = false
) = ReferenceTableValue().apply {
    this.id = id
    this.name = name
    this.sortOrder = sortOrder
    this.isSystemValue = isSystemValue
}

@Suppress("LongParameterList")
fun createZaakafhandelParameters(
    id: Long = 1234L,
    creationDate: ZonedDateTime = ZonedDateTime.now(),
    domein: String = "dummyDomein",
    zaaktypeUUID: UUID = UUID.randomUUID(),
    zaaktypeOmschrijving: String = "dummyZaaktypeOmschrijving",
    einddatumGeplandWaarschuwing: Int? = null
) =
    ZaakafhandelParameters().apply {
        this.id = id
        this.creatiedatum = creationDate
        this.domein = domein
        this.zaakTypeUUID = zaaktypeUUID
        this.zaaktypeOmschrijving = zaaktypeOmschrijving
        this.einddatumGeplandWaarschuwing = einddatumGeplandWaarschuwing
    }
