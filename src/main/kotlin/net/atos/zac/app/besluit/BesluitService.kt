/*
 * SPDX-FileCopyrightText: 2022 Atos, 2024 Lifely
 * SPDX-License-Identifier: EUPL-1.2+
 */
package net.atos.zac.app.besluit

import jakarta.inject.Inject
import net.atos.client.zgw.brc.BrcClientService
import net.atos.client.zgw.brc.model.generated.Besluit
import net.atos.client.zgw.brc.model.generated.BesluitInformatieObject
import net.atos.client.zgw.brc.model.generated.VervalredenEnum
import net.atos.client.zgw.drc.DrcClientService
import net.atos.zac.app.zaak.model.RestBesluitIntrekkenGegevens
import net.atos.zac.util.UriUtil
import org.apache.commons.collections4.CollectionUtils
import java.util.UUID
import java.util.logging.Logger

class BesluitService @Inject constructor(
    private val brcClientService: BrcClientService,
    private val drcClientService: DrcClientService
) {
    companion object {
        private val LOG = Logger.getLogger(BrcClientService::class.java.name)

        private const val WIJZIGEN_BESLUIT_TOELICHTING = "Wijzigen besluit"
    }

    fun readBesluit(restBesluitIntrekkenGegevens: RestBesluitIntrekkenGegevens): Besluit =
        brcClientService.readBesluit(restBesluitIntrekkenGegevens.besluitUuid).apply {
            vervaldatum = restBesluitIntrekkenGegevens.vervaldatum
            vervalreden = VervalredenEnum.fromValue(restBesluitIntrekkenGegevens.vervalreden.lowercase())
        }

    fun withdrawBesluit(besluit: Besluit, reden: String): Besluit =
        brcClientService.updateBesluit(
            besluit,
            getBesluitWithdrawalExplanation(besluit.vervalreden)?.let { String.format(it, reden) }
        )

    fun updateBesluitInformatieobjecten(
        besluit: Besluit,
        nieuweDocumenten: List<UUID>
    ) {
        val besluitInformatieobjecten = brcClientService.listBesluitInformatieobjecten(besluit.url)
        val huidigeDocumenten = besluitInformatieobjecten
            .map { UriUtil.uuidFromURI(it.informatieobject) }
            .toList()
        val verwijderen = CollectionUtils.subtract(huidigeDocumenten, nieuweDocumenten)
        val toevoegen = CollectionUtils.subtract(nieuweDocumenten, huidigeDocumenten)
        verwijderen.forEach { teVerwijderenInformatieobject ->
            besluitInformatieobjecten
                .filter { UriUtil.uuidFromURI(it.informatieobject) == teVerwijderenInformatieobject }
                .forEach { brcClientService.deleteBesluitinformatieobject(UriUtil.uuidFromURI(it.url)) }
        }
        toevoegen.forEach { documentUri ->
            drcClientService.readEnkelvoudigInformatieobject(documentUri).let { enkelvoudigInformatieObject ->
                BesluitInformatieObject().apply {
                    this.informatieobject = enkelvoudigInformatieObject.url
                    this.besluit = besluit.url
                }
            }.let {
                brcClientService.createBesluitInformatieobject(
                    it,
                    WIJZIGEN_BESLUIT_TOELICHTING
                )
            }
        }
    }

    private fun getBesluitWithdrawalExplanation(withdrawalReason: VervalredenEnum): String? {
        return when (withdrawalReason) {
            VervalredenEnum.INGETROKKEN_OVERHEID -> "Overheid: %s"
            VervalredenEnum.INGETROKKEN_BELANGHEBBENDE -> "Belanghebbende: %s"
            else -> {
                LOG.info("Unknown besluit withdrawal reason: '$withdrawalReason'. Returning 'null'.")
                null
            }
        }
    }
}
