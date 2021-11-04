/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.cache.event;

import net.atos.zac.event.AbstractUpdateEvent;

public class CacheUpdateEvent extends AbstractUpdateEvent<Object, Long> {

    private static final long serialVersionUID = -329301003012599689L;

    /**
     * Constructor for the sake of JAXB
     */
    public CacheUpdateEvent() {
        super();
    }

    @Override
    public Object getObjectType() {
        return null;
    }
}
