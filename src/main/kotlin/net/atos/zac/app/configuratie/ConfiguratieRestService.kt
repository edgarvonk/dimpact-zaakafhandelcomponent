/*
 * SPDX-FileCopyrightText: 2022 Atos, 2024 Lifely
 * SPDX-License-Identifier: EUPL-1.2+
 */
package net.atos.zac.app.configuratie

import jakarta.inject.Inject
import jakarta.inject.Singleton
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import net.atos.zac.app.configuratie.model.RestTaal
import net.atos.zac.app.configuratie.model.toRestTaal
import net.atos.zac.app.configuratie.model.toRestTalen
import net.atos.zac.configuratie.ConfiguratieService
import net.atos.zac.util.JsonbUtil
import nl.lifely.zac.util.AllOpen
import nl.lifely.zac.util.NoArgConstructor

/**
 * Provides specific configuration items to a ZAC client.
 */
@Path("configuratie")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Singleton
@AllOpen
@NoArgConstructor
class ConfiguratieRestService @Inject constructor(
    private val configuratieService: ConfiguratieService
) {
    @GET
    @Path("talen")
    fun listTalen(): List<RestTaal> = configuratieService.listTalen().toRestTalen()

    @GET
    @Path("talen/default")
    fun readDefaultTaal(): RestTaal? = configuratieService.findDefaultTaal()?.toRestTaal()

    @GET
    @Path("maxFileSizeMB")
    fun readMaxFileSizeMB(): Long = configuratieService.readMaxFileSizeMB()

    @GET
    @Path("additionalAllowedFileTypes")
    fun readAdditionalAllowedFileTypes(): List<String> = configuratieService.readAdditionalAllowedFileTypes()

    @GET
    @Path("gemeente/code")
    fun readGemeenteCode(): String = JsonbUtil.JSONB.toJson(configuratieService.readGemeenteCode())

    @GET
    @Path("gemeente")
    fun readGemeenteNaam(): String = JsonbUtil.JSONB.toJson(configuratieService.readGemeenteNaam())
}
