package net.atos.client.zgw.drc.model

import net.atos.client.zgw.drc.model.generated.BestandsDeel
import net.atos.client.zgw.drc.model.generated.EnkelvoudigInformatieObject
import java.net.URI
import java.time.OffsetDateTime
import java.util.UUID

@Suppress("LongParameterList")
fun createEnkelvoudigInformatieObject(
    url: URI = URI("http://example.com/${UUID.randomUUID()}"),
    versie: Int = 1234,
    beginRegistratie: OffsetDateTime = OffsetDateTime.now(),
    inhoud: URI = URI("http://example.com/${UUID.randomUUID()}"),
    locked: Boolean = false,
    bestandsdelen: List<BestandsDeel> = emptyList()
) = EnkelvoudigInformatieObject(
    url,
    versie,
    beginRegistratie,
    inhoud,
    locked,
    bestandsdelen
)
