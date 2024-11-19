package net.atos.zac.zoeken.model

import net.atos.zac.zoeken.model.zoekobject.ZaakZoekObject
import net.atos.zac.zoeken.model.zoekobject.ZoekObjectType
import java.util.UUID

fun createZaakZoekObject(
    uuidAsString: String = UUID.randomUUID().toString(),
    type: ZoekObjectType = ZoekObjectType.ZAAK,
    zaaktypeOmschrijving: String = "dummyOmschrijving"
) = ZaakZoekObject(
    id = uuidAsString,
    type = type.name
).apply {
    this.zaaktypeOmschrijving = zaaktypeOmschrijving
}
