/*
 * SPDX-FileCopyrightText: 2022 Atos, 2024 Lifely
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.admin.converter;

import java.util.List;

import net.atos.zac.admin.model.ReferenceTableValue;
import net.atos.zac.app.admin.model.RestReferenceTableValue;

public final class RestReferenceValueConverter {

    public static RestReferenceTableValue convert(final ReferenceTableValue referenceTableValue) {
        final RestReferenceTableValue restReferenceTableValue = new RestReferenceTableValue();
        restReferenceTableValue.id = referenceTableValue.getId();
        restReferenceTableValue.naam = referenceTableValue.getNaam();
        return restReferenceTableValue;
    }

    public static List<String> convert(final List<ReferenceTableValue> referentieTabelWaarden) {
        return referentieTabelWaarden.stream()
                .map(ReferenceTableValue::getNaam)
                .toList();
    }

    public static ReferenceTableValue convert(final RestReferenceTableValue restReferenceTableValue) {
        final ReferenceTableValue referenceTableValue = new ReferenceTableValue();
        referenceTableValue.setId(restReferenceTableValue.id);
        referenceTableValue.setNaam(restReferenceTableValue.naam);
        return referenceTableValue;
    }
}