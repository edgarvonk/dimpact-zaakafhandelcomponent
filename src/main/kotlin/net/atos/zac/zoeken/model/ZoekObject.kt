/*
 * SPDX-FileCopyrightText: 2021 Atos, 2024 Lifely
 * SPDX-License-Identifier: EUPL-1.2+
 */
package net.atos.zac.zoeken.model

import net.atos.zac.zoeken.model.index.ZoekObjectType

interface ZoekObject {
    companion object {
        const val IS_TOEGEKEND_FIELD: String = "isToegekend"
    }

    fun getObjectId(): String

    fun getType(): ZoekObjectType
}
