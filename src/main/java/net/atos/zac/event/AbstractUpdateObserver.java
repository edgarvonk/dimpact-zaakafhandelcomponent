/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.event;

/**
 * Generic code for beans that listen to AbstractUpdateEvents.
 */
public abstract class AbstractUpdateObserver<UPDATE_EVENT extends AbstractUpdateEvent> {
    public abstract void onFire(final UPDATE_EVENT event);
}
