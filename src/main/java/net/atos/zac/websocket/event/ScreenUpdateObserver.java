/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.websocket.event;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.ManagedBean;
import javax.ejb.EJB;
import javax.enterprise.event.ObservesAsync;
import javax.websocket.Session;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.atos.zac.event.AbstractUpdateObserver;
import net.atos.zac.websocket.SessionRegistry;

/**
 * This bean listens for {@link ScreenUpdateEvent}, converts them to a Websockets event and then forwards it to the browsers that have subscribed to it.
 */
@ManagedBean
public class ScreenUpdateObserver extends AbstractUpdateObserver<ScreenUpdateEvent> {

    private static final Logger LOG = Logger.getLogger(ScreenUpdateObserver.class.getName());

    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

    @EJB
    private SessionRegistry sessionRegistry;

    public void onFire(final @ObservesAsync ScreenUpdateEvent event) {
        sendToWebsocketSubscribers(event);
    }

    private void sendToWebsocketSubscribers(final ScreenUpdateEvent event) {
        try {
            final Set<Session> subscribers = sessionRegistry.listSessions(event);
            if (!subscribers.isEmpty()) {
                final String json = JSON_MAPPER.writeValueAsString(event);
                subscribers.forEach(session -> session.getAsyncRemote().sendText(json));
            }
        } catch (final JsonProcessingException e) {
            LOG.log(Level.SEVERE, "Failed to convert the ScreenUpdateEvent to JSON.", e);
        }
    }
}
