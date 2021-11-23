/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.shared.cache.event;

import static net.atos.zac.event.Opcode.UPDATED;

import java.net.URI;
import java.util.UUID;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;

import net.atos.client.zgw.shared.util.URIUtil;
import net.atos.zac.event.AbstractEvent;
import net.atos.zac.event.Opcode;

public class CacheEvent extends AbstractEvent<CacheEventType, URI> {

    private static final long serialVersionUID = -329301003012599689L;

    @NotNull
    private CacheEventType objectType;

    private volatile UUID uuid = null;

    /**
     * Constructor for the sake of JAXB
     */
    public CacheEvent() {
        super();
    }

    /**
     * Constructor die alle verplichte velden bevat.
     *
     * @param objectType het type object waarop de operatie is uitgevoerd
     * @param objectId   de identificatie van het object waarop een operatie is uitgevoerd
     */
    public CacheEvent(final CacheEventType objectType, final URI objectId) {
        super(UPDATED, objectId);
        this.objectType = objectType;
    }

    @Override
    public CacheEventType getObjectType() {
        return objectType;
    }

    public URI getUrl() {
        return getObjectId();
    }

    public UUID getUUID() {
        if (uuid == null) {
            uuid = URIUtil.parseUUIDFromResourceURI(getObjectId());
        }
        return uuid;
    }

    @AssertTrue(message = "Cache entries for CREATED objectIds cannot exist (the new id has not yet been cached)")
    boolean isValid() {
        return getOperation() != Opcode.CREATED;
    }
}
