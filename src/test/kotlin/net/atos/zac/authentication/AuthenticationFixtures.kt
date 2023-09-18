package net.atos.zac.authentication

import net.atos.zac.app.zaken.model.ZAAK_TYPE_1_OMSCHRIJVING
import net.atos.zac.app.zaken.model.ZAAK_TYPE_2_OMSCHRIJVING

@Suppress("LongParameterList")
fun createLoggedInUser(
    id: String = "dummyId",
    firstName: String = "dummyFirstName",
    lastName: String = "dummyLastName",
    displayName: String = "dummyDisplayName",
    email: String = "dummy@example.com",
    roles: Set<String> = setOf("dummyRole1", "dummyRole2"),
    groups: Set<String> = setOf("dummyGroup1", "dummyGroup2"),
    zaakTypes: Set<String> = setOf(ZAAK_TYPE_1_OMSCHRIJVING, ZAAK_TYPE_2_OMSCHRIJVING)
) = LoggedInUser(
    id,
    firstName,
    lastName,
    displayName,
    email,
    roles,
    groups,
    zaakTypes
)
