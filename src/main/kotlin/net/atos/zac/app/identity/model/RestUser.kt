/*
 * SPDX-FileCopyrightText: 2024 Lifely
 * SPDX-License-Identifier: EUPL-1.2+
 */
package net.atos.zac.app.identity.model

import net.atos.zac.identity.model.User
import net.atos.zac.identity.model.getFullName
import nl.lifely.zac.util.AllOpen
import nl.lifely.zac.util.NoArgConstructor

@NoArgConstructor
@AllOpen
data class RestUser(
    var id: String,

    var naam: String
)

fun User.toRestUser() =
    RestUser(
        this.id,
        this.getFullName()
    )

fun List<User>.toRestUsers(): List<RestUser> =
    this.map { it.toRestUser() }
