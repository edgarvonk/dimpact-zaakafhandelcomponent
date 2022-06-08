/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.zoeken.model;

import java.util.Map;

import net.atos.zac.zoeken.model.DatumVeld;
import net.atos.zac.zoeken.model.FilterVeld;
import net.atos.zac.zoeken.model.SorteerVeld;
import net.atos.zac.zoeken.model.ZoekVeld;

public class RESTZoekParameters {
    public Map<ZoekVeld, String> zoeken;

    public Map<FilterVeld, String> filters;

    public Map<DatumVeld, RESTDatumRange> datums;

    public Map<String, String> filterQueries;

    public SorteerVeld sorteerVeld;

    public String sorteerRichting;

    public int rows;

    public int page;
}
