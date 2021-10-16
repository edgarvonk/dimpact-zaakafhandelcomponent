/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.or.shared.exception;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;

import net.atos.client.or.shared.model.Fout;

/**
 *
 */
@Provider
public class FoutExceptionMapper implements ResponseExceptionMapper<FoutException> {

    @Override
    public boolean handles(final int status, final MultivaluedMap<String, Object> headers) {
        return status > Response.Status.BAD_REQUEST.getStatusCode() && status < Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
    }

    @Override
    public FoutException toThrowable(final Response response) {
        return new FoutException(response.readEntity(Fout.class));
    }
}
