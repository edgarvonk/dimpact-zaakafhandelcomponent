/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zoeken.model;

import java.util.Map;

import net.atos.zac.shared.model.SorteerRichting;
import net.atos.zac.zoeken.model.FilterVeld;
import net.atos.zac.zoeken.model.SorteerVeld;
import net.atos.zac.zoeken.model.ZoekVeld;

public class RESTZoekParameters {
    public Map<ZoekVeld, String> zoeken;

    public Map<FilterVeld, String> filters;

    public SorteerVeld sorteerVeld;

    public SorteerRichting sorteerRichting;

    public int rows;

    public int start;
}
