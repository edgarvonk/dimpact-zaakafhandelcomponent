/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.klanten.converter;

import org.apache.commons.collections4.CollectionUtils;

import net.atos.client.kvk.vestigingsprofiel.model.SBIActiviteit;
import net.atos.client.kvk.vestigingsprofiel.model.Vestiging;
import net.atos.zac.app.klanten.model.bedrijven.RESTAdres;
import net.atos.zac.app.klanten.model.bedrijven.RESTVestigingsprofiel;

public class RESTVestigingsprofielConverter {

    public RESTVestigingsprofiel convert(final Vestiging vestiging) {
        final RESTVestigingsprofiel restVestigingsprofiel = new RESTVestigingsprofiel();
        restVestigingsprofiel.kvkNummer = vestiging.getKvkNummer();
        restVestigingsprofiel.vestigingsnummer = vestiging.getVestigingsnummer();
        restVestigingsprofiel.handelsnaam = vestiging.getEersteHandelsnaam();
        restVestigingsprofiel.rsin = vestiging.getRsin();
        restVestigingsprofiel.totaalWerkzamePersonen = vestiging.getTotaalWerkzamePersonen();
        restVestigingsprofiel.deeltijdWerkzamePersonen = vestiging.getDeeltijdWerkzamePersonen();
        restVestigingsprofiel.voltijdWerkzamePersonen = vestiging.getVoltijdWerkzamePersonen();
        restVestigingsprofiel.commercieleVestiging = indicatie(vestiging.getIndCommercieleVestiging());

        restVestigingsprofiel.type = indicatie(vestiging.getIndHoofdvestiging()) ? "HOOFDVESTIGING" : "NEVENVESTIGING";
        restVestigingsprofiel.sbiHoofdActiviteit =
                vestiging.getSbiActiviteiten()
                        .stream()
                        .filter(a -> indicatie(a.getIndHoofdactiviteit()))
                        .findAny()
                        .map(SBIActiviteit::getSbiOmschrijving)
                        .orElse(null);

        restVestigingsprofiel.sbiActiviteiten =
                vestiging.getSbiActiviteiten()
                        .stream()
                        .filter(a -> !indicatie(a.getIndHoofdactiviteit()))
                        .map(SBIActiviteit::getSbiOmschrijving)
                        .toList();

        restVestigingsprofiel.adressen = vestiging.getAdressen()
                .stream()
                .map(adres -> new RESTAdres(adres.getType(),
                                            indicatie(adres.getIndAfgeschermd()),
                                            adres.getVolledigAdres()))
                .toList();

        restVestigingsprofiel.website = CollectionUtils.emptyIfNull(vestiging.getWebsites()).stream().findFirst().orElse(null);
        return restVestigingsprofiel;
    }

    public boolean indicatie(String stringIndicatie) {
        if (stringIndicatie == null) {
            return false;
        }
        if (stringIndicatie.equalsIgnoreCase("ja")) {
            return true;
        }
        if (stringIndicatie.equalsIgnoreCase("nee")) {
            return false;
        }
        throw new IllegalArgumentException("Onbekende waarde voor indicatie \"%s\"".formatted(stringIndicatie));
    }
}
