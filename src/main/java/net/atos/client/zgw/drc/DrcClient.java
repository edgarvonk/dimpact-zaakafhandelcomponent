/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.zgw.drc;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM;

import java.util.List;
import java.util.UUID;

import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import net.atos.client.zgw.drc.exception.DrcRuntimeExceptionMapper;
import net.atos.client.zgw.drc.model.EnkelvoudigInformatieobjectListParameters;
import net.atos.client.zgw.drc.model.Lock;
import net.atos.client.zgw.drc.model.ObjectInformatieobjectListParameters;
import net.atos.client.zgw.drc.model.generated.EnkelvoudigInformatieObject;
import net.atos.client.zgw.drc.model.generated.EnkelvoudigInformatieObjectCreateLockRequest;
import net.atos.client.zgw.drc.model.generated.EnkelvoudigInformatieObjectWithLockRequest;
import net.atos.client.zgw.drc.model.generated.Gebruiksrechten;
import net.atos.client.zgw.drc.model.generated.ObjectInformatieObject;
import net.atos.client.zgw.shared.exception.ZgwFoutExceptionMapper;
import net.atos.client.zgw.shared.exception.ZgwValidatieFoutExceptionMapper;
import net.atos.client.zgw.shared.model.Results;
import net.atos.client.zgw.shared.model.audit.AuditTrailRegel;
import net.atos.client.zgw.shared.util.JsonbConfiguration;
import net.atos.client.zgw.shared.util.ZGWClientHeadersFactory;

@RegisterRestClient(configKey = "ZGW-API-Client")
@RegisterClientHeaders(ZGWClientHeadersFactory.class)
@RegisterProvider(ZgwFoutExceptionMapper.class)
@RegisterProvider(ZgwValidatieFoutExceptionMapper.class)
@RegisterProvider(DrcRuntimeExceptionMapper.class)
@RegisterProvider(JsonbConfiguration.class)
@Path("documenten/api/v1")
@Produces(APPLICATION_JSON)
public interface DrcClient {

    @POST
    @Path("enkelvoudiginformatieobjecten")
    EnkelvoudigInformatieObject enkelvoudigInformatieobjectCreate(
            final EnkelvoudigInformatieObjectCreateLockRequest enkelvoudigInformatieObjectCreateLockRequest
    );

    @GET
    @Path("enkelvoudiginformatieobjecten")
    Results<EnkelvoudigInformatieObject> enkelvoudigInformatieobjectList(
            @BeanParam final EnkelvoudigInformatieobjectListParameters parameters
    );

    @GET
    @Path("enkelvoudiginformatieobjecten/{uuid}")
    EnkelvoudigInformatieObject enkelvoudigInformatieobjectRead(@PathParam("uuid") final UUID uuid);

    @GET
    @Path("enkelvoudiginformatieobjecten/{uuid}")
    EnkelvoudigInformatieObject enkelvoudigInformatieobjectReadVersie(
            @PathParam("uuid") final UUID uuid,
            @QueryParam("versie") final Integer versie
    );

    @GET
    @Produces(APPLICATION_OCTET_STREAM)
    @Path("enkelvoudiginformatieobjecten/{uuid}/download")
    Response enkelvoudigInformatieobjectDownload(@PathParam("uuid") final UUID uuid);

    @GET
    @Produces(APPLICATION_OCTET_STREAM)
    @Path("enkelvoudiginformatieobjecten/{uuid}/download")
    Response enkelvoudigInformatieobjectDownloadVersie(@PathParam("uuid") final UUID uuid, @QueryParam("versie") final Integer versie);

    @PATCH
    @Path("enkelvoudiginformatieobjecten/{uuid}")
    EnkelvoudigInformatieObject enkelvoudigInformatieobjectPartialUpdate(
            @PathParam("uuid") final UUID uuid,
            final EnkelvoudigInformatieObjectWithLockRequest enkelvoudigInformatieObjectWithLockRequest
    );

    @DELETE
    @Path("enkelvoudiginformatieobjecten/{uuid}")
    Response enkelvoudigInformatieobjectDelete(@PathParam("uuid") final UUID uuid);

    @POST
    @Path("enkelvoudiginformatieobjecten/{uuid}/lock")
    Lock enkelvoudigInformatieobjectLock(
            @PathParam("uuid") final UUID uuid,
            final Lock enkelvoudigInformatieObjectLock
    );

    @POST
    @Path("enkelvoudiginformatieobjecten/{uuid}/unlock")
    Response enkelvoudigInformatieobjectUnlock(@PathParam("uuid") final UUID uuid, final Lock lock);

    @POST
    @Path("gebruiksrechten")
    Gebruiksrechten gebruiksrechtenCreate(final Gebruiksrechten gebruiksrechten);

    @GET
    @Path("objectinformatieobjecten")
    Results<ObjectInformatieObject> objectInformatieobjectList(@BeanParam final ObjectInformatieobjectListParameters parameters);

    @GET
    @Path("enkelvoudiginformatieobjecten/{uuid}/audittrail")
    List<AuditTrailRegel> listAuditTrail(@PathParam("uuid") UUID enkelvoudigInformatieobjectUUID);
}
