/*
 * SPDX-FileCopyrightText: 2023 Lifely
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.policy.output

fun createDocumentRechten() = DocumentRechten(
    true,
    true,
    true,
    true,
    true,
    true
)

fun createTaakRechten() = TaakRechten(
    true,
    true,
    true,
    true
)

fun createZaakRechten() = ZaakRechten(
    true,
    true,
    true,
    true,
    true,
    true,
    true,
    true
)

fun createWerklijstRechten(
    inbox: Boolean = true,
    ontkoppeldeDocumentenVerwijderen: Boolean = true,
    inboxProductaanvragenVerwijderen: Boolean = true,
    zakenTaken: Boolean = true,
    zakenTakenVerdelen: Boolean = true
) = WerklijstRechten(
    inbox,
    ontkoppeldeDocumentenVerwijderen,
    inboxProductaanvragenVerwijderen,
    zakenTaken,
    zakenTakenVerdelen
)
