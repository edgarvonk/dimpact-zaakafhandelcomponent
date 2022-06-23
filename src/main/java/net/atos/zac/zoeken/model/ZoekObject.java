/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.zoeken.model;

import net.atos.zac.zoeken.model.index.ZoekObjectType;

public interface ZoekObject {
    ZoekObjectType getType();
}
