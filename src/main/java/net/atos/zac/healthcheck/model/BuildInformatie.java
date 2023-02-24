/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.healthcheck.model;

import java.time.LocalDateTime;

public class BuildInformatie {

    private final String buildId;

    private final LocalDateTime buildDatumTijd;

    private final String versienummer;

    public BuildInformatie(final String buildId, final LocalDateTime buildDatumTijd, final String versienummer) {
        this.buildId = buildId;
        this.buildDatumTijd = buildDatumTijd;
        this.versienummer = versienummer;
    }

    public String getBuildId() {
        return buildId;
    }

    public LocalDateTime getBuildDatumTijd() {
        return buildDatumTijd;
    }

    public String getVersienummer() {
        return versienummer;
    }
}
