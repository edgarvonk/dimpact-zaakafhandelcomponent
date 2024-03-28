/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */
package net.atos.zac.app.signaleringen

import jakarta.enterprise.inject.Instance
import jakarta.inject.Inject
import jakarta.inject.Singleton
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.GET
import jakarta.ws.rs.PUT
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import net.atos.client.zgw.drc.DRCClientService
import net.atos.client.zgw.drc.model.generated.EnkelvoudigInformatieObject
import net.atos.client.zgw.zrc.ZRCClientService
import net.atos.client.zgw.zrc.model.Zaak
import net.atos.zac.app.informatieobjecten.converter.RESTInformatieobjectConverter
import net.atos.zac.app.informatieobjecten.model.RESTEnkelvoudigInformatieobject
import net.atos.zac.app.signaleringen.converter.RESTSignaleringInstellingenConverter
import net.atos.zac.app.signaleringen.model.RESTSignaleringInstellingen
import net.atos.zac.app.taken.converter.RESTTaakConverter
import net.atos.zac.app.taken.model.RESTTaak
import net.atos.zac.app.zaken.converter.RESTZaakOverzichtConverter
import net.atos.zac.app.zaken.model.RESTZaakOverzicht
import net.atos.zac.authentication.LoggedInUser
import net.atos.zac.flowable.TakenService
import net.atos.zac.identity.IdentityService
import net.atos.zac.signalering.SignaleringenService
import net.atos.zac.signalering.model.Signalering
import net.atos.zac.signalering.model.SignaleringInstellingen
import net.atos.zac.signalering.model.SignaleringInstellingenZoekParameters
import net.atos.zac.signalering.model.SignaleringSubject
import net.atos.zac.signalering.model.SignaleringType
import net.atos.zac.signalering.model.SignaleringZoekParameters
import org.flowable.task.api.TaskInfo
import java.time.ZonedDateTime
import java.util.UUID

@Path("signaleringen")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Singleton
@Suppress("LongParameterList")
class SignaleringenRestService @Inject constructor(
    private val signaleringenService: SignaleringenService,
    private val zrcClientService: ZRCClientService,
    private val takenService: TakenService,
    private val drcClientService: DRCClientService,
    private val identityService: IdentityService,
    private val restZaakOverzichtConverter: RESTZaakOverzichtConverter,
    private val restTaakConverter: RESTTaakConverter,
    private val restInformatieobjectConverter: RESTInformatieobjectConverter,
    private val restSignaleringInstellingenConverter: RESTSignaleringInstellingenConverter,
    private val loggedInUserInstance: Instance<LoggedInUser>,
) {
    @GET
    @Path("/latest")
    fun latestSignaleringen(): ZonedDateTime {
        val parameters = SignaleringZoekParameters(loggedInUserInstance.get())
        return signaleringenService.latestSignalering(parameters)
    }

    @GET
    @Path("/zaken/{type}")
    fun listZakenSignaleringen(
        @PathParam("type") signaleringsType: SignaleringType.Type
    ): List<RESTZaakOverzicht> {
        val parameters = SignaleringZoekParameters(loggedInUserInstance.get())
            .types(signaleringsType)
            .subjecttype(SignaleringSubject.ZAAK)
        return signaleringenService.listSignaleringen(parameters).stream()
            .map { signalering: Signalering -> zrcClientService.readZaak(UUID.fromString(signalering.subject)) }
            .map { zaak: Zaak? -> restZaakOverzichtConverter.convert(zaak!!) }
            .toList()
    }

    @GET
    @Path("/taken/{type}")
    fun listTakenSignaleringen(@PathParam("type") signaleringsType: SignaleringType.Type): List<RESTTaak> {
        val parameters = SignaleringZoekParameters(loggedInUserInstance.get())
            .types(signaleringsType)
            .subjecttype(SignaleringSubject.TAAK)
        return signaleringenService.listSignaleringen(parameters).stream()
            .map { signalering: Signalering -> takenService.readTask(signalering.subject) }
            .map { taskInfo: TaskInfo? -> restTaakConverter.convert(taskInfo) }
            .toList()
    }

    @GET
    @Path("/informatieobjecten/{type}")
    fun listInformatieobjectenSignaleringen(
        @PathParam("type") signaleringsType: SignaleringType.Type
    ): List<RESTEnkelvoudigInformatieobject> {
        val parameters = SignaleringZoekParameters(loggedInUserInstance.get())
            .types(signaleringsType)
            .subjecttype(SignaleringSubject.DOCUMENT)
        return signaleringenService.listSignaleringen(parameters).stream()
            .map { signalering: Signalering ->
                drcClientService.readEnkelvoudigInformatieobject(
                    UUID.fromString(signalering.subject)
                )
            }
            .map { enkelvoudigInformatieObject: EnkelvoudigInformatieObject? ->
                restInformatieobjectConverter.convertToREST(
                    enkelvoudigInformatieObject
                )
            }
            .toList()
    }

    @GET
    @Path("/instellingen")
    fun listUserSignaleringInstellingen(): List<RESTSignaleringInstellingen> {
        val parameters = SignaleringInstellingenZoekParameters(
            loggedInUserInstance.get()
        )
        return restSignaleringInstellingenConverter.convert(
            signaleringenService.listInstellingenInclusiefMogelijke(parameters)
        )
    }

    @PUT
    @Path("/instellingen")
    fun updateUserSignaleringInstellingen(restInstellingen: RESTSignaleringInstellingen) {
        signaleringenService.createUpdateOrDeleteInstellingen(
            restSignaleringInstellingenConverter.convert(restInstellingen, loggedInUserInstance.get())
        )
    }

    @GET
    @Path("group/{groupId}/instellingen")
    fun listGroupSignaleringInstellingen(
        @PathParam("groupId") groupId: String
    ): List<RESTSignaleringInstellingen> {
        val group = identityService.readGroup(groupId)
        val parameters = SignaleringInstellingenZoekParameters(group)
        return restSignaleringInstellingenConverter.convert(
            signaleringenService.listInstellingenInclusiefMogelijke(parameters)
        )
    }

    @PUT
    @Path("group/{groupId}/instellingen")
    fun updateGroupSignaleringInstellingen(
        @PathParam("groupId") groupId: String,
        restInstellingen: RESTSignaleringInstellingen
    ) {
        val group = identityService.readGroup(groupId)
        signaleringenService.createUpdateOrDeleteInstellingen(
            restSignaleringInstellingenConverter.convert(restInstellingen, group)
        )
    }

    @GET
    @Path("/typen/dashboard")
    fun listDashboardSignaleringTypen(): List<SignaleringType.Type> {
        val parameters = SignaleringInstellingenZoekParameters(
            loggedInUserInstance.get()
        )
            .dashboard()
        return signaleringenService.listInstellingen(parameters).stream()
            .map { instellingen: SignaleringInstellingen -> instellingen.type.type }
            .toList()
    }
}
