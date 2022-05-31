/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.client.sd;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import net.atos.client.sd.model.Deposit;
import net.atos.client.sd.model.UnattendedResponse;
import net.atos.client.sd.model.WizardResponse;

@RegisterRestClient(configKey = "SD-Client")
@Path("wsxmldeposit")
@Produces(APPLICATION_JSON)
public interface SmartDocumentsClient {

    @POST
    @Path("deposit/unattended")
    UnattendedResponse unattendedDeposit(@HeaderParam("Authorization") final String authenticationHeader, @HeaderParam("Username") final String username,
            final Deposit deposit);

    @POST
    @Path("deposit/wizard")
    WizardResponse wizardDeposit(@HeaderParam("Authorization") final String authenticationHeader, @HeaderParam("Username") final String username,
            final Deposit deposit);
}
