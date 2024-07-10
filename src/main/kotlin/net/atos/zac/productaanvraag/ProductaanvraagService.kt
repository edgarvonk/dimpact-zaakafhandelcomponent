/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */
package net.atos.zac.productaanvraag

import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.json.bind.JsonbBuilder
import jakarta.json.bind.JsonbConfig
import net.atos.client.or.`object`.ObjectsClientService
import net.atos.client.or.`object`.model.ORObject
import net.atos.client.vrl.VrlClientService
import net.atos.client.zgw.drc.DrcClientService
import net.atos.client.zgw.shared.ZGWApiService
import net.atos.client.zgw.zrc.ZRCClientService
import net.atos.client.zgw.zrc.model.Medewerker
import net.atos.client.zgw.zrc.model.NatuurlijkPersoon
import net.atos.client.zgw.zrc.model.OrganisatorischeEenheid
import net.atos.client.zgw.zrc.model.RolMedewerker
import net.atos.client.zgw.zrc.model.RolNatuurlijkPersoon
import net.atos.client.zgw.zrc.model.RolOrganisatorischeEenheid
import net.atos.client.zgw.zrc.model.RolVestiging
import net.atos.client.zgw.zrc.model.Vestiging
import net.atos.client.zgw.zrc.model.Zaak
import net.atos.client.zgw.zrc.model.ZaakInformatieobject
import net.atos.client.zgw.zrc.model.zaakobjecten.ZaakobjectProductaanvraag
import net.atos.client.zgw.ztc.ZtcClientService
import net.atos.client.zgw.ztc.model.generated.OmschrijvingGeneriekEnum
import net.atos.client.zgw.ztc.model.generated.ZaakType
import net.atos.zac.configuratie.ConfiguratieService
import net.atos.zac.documenten.InboxDocumentenService
import net.atos.zac.flowable.BPMNService
import net.atos.zac.flowable.CMMNService
import net.atos.zac.identity.IdentityService
import net.atos.zac.productaanvraag.model.InboxProductaanvraag
import net.atos.zac.productaanvraag.model.generated.Betrokkene
import net.atos.zac.productaanvraag.model.generated.Geometry
import net.atos.zac.productaanvraag.model.generated.ProductaanvraagDimpact
import net.atos.zac.productaanvraag.util.BetalingStatusEnumJsonAdapter
import net.atos.zac.productaanvraag.util.GeometryTypeEnumJsonAdapter
import net.atos.zac.productaanvraag.util.IndicatieMachtigingEnumJsonAdapter
import net.atos.zac.productaanvraag.util.RolOmschrijvingGeneriekEnumJsonAdapter
import net.atos.zac.productaanvraag.util.convertToZgwPoint
import net.atos.zac.util.JsonbUtil
import net.atos.zac.util.UriUtil.uuidFromURI
import net.atos.zac.zaaksturing.ZaakafhandelParameterBeheerService
import net.atos.zac.zaaksturing.ZaakafhandelParameterService
import net.atos.zac.zaaksturing.model.ZaakafhandelParameters
import nl.lifely.zac.util.AllOpen
import nl.lifely.zac.util.NoArgConstructor
import java.net.URI
import java.time.LocalDate
import java.util.UUID
import java.util.logging.Level
import java.util.logging.Logger

@ApplicationScoped
@NoArgConstructor
@AllOpen
@Suppress("TooManyFunctions", "LongParameterList")
class ProductaanvraagService @Inject constructor(
    private val objectsClientService: ObjectsClientService,
    private val zgwApiService: ZGWApiService,
    private val zrcClientService: ZRCClientService,
    private val drcClientService: DrcClientService,
    private val ztcClientService: ZtcClientService,
    private val vrlClientService: VrlClientService,
    private val identityService: IdentityService,
    private val zaakafhandelParameterService: ZaakafhandelParameterService,
    private val zaakafhandelParameterBeheerService: ZaakafhandelParameterBeheerService,
    private val inboxDocumentenService: InboxDocumentenService,
    private val inboxProductaanvraagService: InboxProductaanvraagService,
    private val cmmnService: CMMNService,
    private val bpmnService: BPMNService,
    private val configuratieService: ConfiguratieService
) {
    companion object {
        private val LOG = Logger.getLogger(ProductaanvraagService::class.java.name)

        private const val AANVRAAG_PDF_TITEL = "Aanvraag PDF"
        private const val AANVRAAG_PDF_BESCHRIJVING = "PDF document met de aanvraag gegevens van de zaak"
        private const val BPMN_PROCESS_DEFINITION_KEY = "test-met-proces"
        private const val PRODUCTAANVRAAG_FORMULIER_VELD_AANVRAAGGEGEVENS = "aanvraaggegevens"
        private const val PRODUCTAANVRAAG_FORMULIER_VELD_BRON = "bron"
        private const val PRODUCTAANVRAAG_FORMULIER_VELD_TYPE = "type"
        private const val ROL_TOELICHTING = "Overgenomen vanuit de product aanvraag"

        /**
         * Maximum length of the description field in a zaak as defined by the ZGW ZRC API.
         */
        private const val ZAAK_DESCRIPTION_MAX_LENGTH = 80
        private const val ZAAK_INFORMATIEOBJECT_REDEN =
            "Document toegevoegd tijdens het starten van de van de zaak vanuit een product aanvraag"
    }

    @Suppress("TooGenericExceptionCaught")
    fun handleProductaanvraag(productaanvraagObjectUUID: UUID) {
        try {
            objectsClientService.readObject(productaanvraagObjectUUID).let {
                if (isProductaanvraagDimpact(it)) {
                    LOG.info { "Handle productaanvraag-Dimpact object UUID: $productaanvraagObjectUUID" }
                    handleProductaanvraagDimpact(it)
                }
            }
        } catch (exception: RuntimeException) {
            LOG.log(
                Level.WARNING,
                "Failed to handle productaanvraag-Dimpact object UUID: $productaanvraagObjectUUID",
                exception
            )
        }
    }

    fun getAanvraaggegevens(productaanvraagObject: ORObject): Map<String, Any> {
        val aanvraaggegevensKeyValueMap = mutableMapOf<String, Any>()
        (productaanvraagObject.record.data[PRODUCTAANVRAAG_FORMULIER_VELD_AANVRAAGGEGEVENS] as Map<*, *>).values.forEach {
            aanvraaggegevensKeyValueMap.putAll(it as Map<String, Any>)
        }
        return aanvraaggegevensKeyValueMap
    }

    @Suppress("TooGenericExceptionCaught", "TooGenericExceptionThrown")
    fun getProductaanvraag(productaanvraagObject: ORObject): ProductaanvraagDimpact =
        try {
            JsonbBuilder.create(
                JsonbConfig()
                    // register our custom enum JSON adapters because by default enums are deserialized using the enum's name
                    // instead of the value and this fails because in the generated model classes the enum names are
                    // capitalized and the values are not
                    .withAdapters(
                        IndicatieMachtigingEnumJsonAdapter(),
                        RolOmschrijvingGeneriekEnumJsonAdapter(),
                        BetalingStatusEnumJsonAdapter(),
                        GeometryTypeEnumJsonAdapter()
                    )
            ).use {
                it.fromJson(
                    JsonbUtil.JSONB.toJson(productaanvraagObject.record.data),
                    ProductaanvraagDimpact::class.java
                )
            }
        } catch (exception: Exception) {
            throw RuntimeException(exception)
        }

    private fun startZaakWithCmmnProcess(
        zaaktypeUuid: UUID,
        productaanvraag: ProductaanvraagDimpact,
        productaanvraagObject: ORObject
    ) {
        val formulierData = getAanvraaggegevens(productaanvraagObject)
        val zaaktype = ztcClientService.readZaaktype(zaaktypeUuid)
        val communicatieKanaal = vrlClientService.findCommunicatiekanaal(
            ConfiguratieService.COMMUNICATIEKANAAL_EFORMULIER
        )
        val createdZaak = Zaak().apply {
            this.zaaktype = zaaktype.url
            omschrijving = getZaakOmschrijving(productaanvraag)
            startdatum = productaanvraagObject.record.startAt
            bronorganisatie = ConfiguratieService.BRON_ORGANISATIE
            communicatieKanaal.ifPresent { communicatiekanaal = it.url }
            verantwoordelijkeOrganisatie = ConfiguratieService.BRON_ORGANISATIE
            productaanvraag.zaakgegevens?.let { zaakgegevens ->
                if (zaakgegevens.geometry != null && zaakgegevens.geometry.type == Geometry.Type.POINT) {
                    zaakgeometrie = zaakgegevens.geometry.convertToZgwPoint()
                }
            }
            // note that we leave the 'toelichting' field empty for a zaak created from a productaanvraag
        }.let(zgwApiService::createZaak)

        LOG.fine("Creating zaak using the ZGW API: $createdZaak")
        val zaakafhandelParameters = zaakafhandelParameterService.readZaakafhandelParameters(zaaktypeUuid)
        assignZaak(createdZaak, zaakafhandelParameters)
        pairProductaanvraagInfoWithZaak(productaanvraag, productaanvraagObject, createdZaak)
        cmmnService.startCase(createdZaak, zaaktype, zaakafhandelParameters, formulierData)
    }

    fun pairProductaanvraagWithZaak(productaanvraag: ORObject, zaakUrl: URI) {
        ZaakobjectProductaanvraag(zaakUrl, productaanvraag.url)
            .let(zrcClientService::createZaakobject)
    }

    fun pairAanvraagPDFWithZaak(productaanvraag: ProductaanvraagDimpact, zaakUrl: URI) {
        ZaakInformatieobject().apply {
            informatieobject = productaanvraag.pdf
            zaak = zaakUrl
            titel = AANVRAAG_PDF_TITEL
            beschrijving = AANVRAAG_PDF_BESCHRIJVING
        }.let {
            LOG.fine("Creating zaak informatieobject: $it")
            zrcClientService.createZaakInformatieobject(it, ZAAK_INFORMATIEOBJECT_REDEN)
        }
    }

    fun pairBijlagenWithZaak(bijlageURIs: List<URI>, zaakUrl: URI) =
        bijlageURIs.forEach {
            val bijlage = drcClientService.readEnkelvoudigInformatieobject(it)
            val zaakInformatieobject = ZaakInformatieobject()
            zaakInformatieobject.informatieobject = bijlage.url
            zaakInformatieobject.zaak = zaakUrl
            zaakInformatieobject.titel = bijlage.titel
            zaakInformatieobject.beschrijving = bijlage.beschrijving
            zrcClientService.createZaakInformatieobject(zaakInformatieobject, ZAAK_INFORMATIEOBJECT_REDEN)
        }

    private fun addBetrokkenen(
        productaanvraag: ProductaanvraagDimpact,
        zaak: Zaak
    ) {
        // only one initiator per zaak is supported so in case there are multiple we only take the first one
        productaanvraag.betrokkenen?.first {
            it.rolOmschrijvingGeneriek == Betrokkene.RolOmschrijvingGeneriek.INITIATOR
        }?.let { initiatorBetrokkene ->
            when {
                initiatorBetrokkene.inpBsn != null -> {
                    addNatuurlijkPersoonInitiatorRole(
                        initiatorBetrokkene.inpBsn,
                        zaak.url,
                        zaak.zaaktype
                    )
                }

                initiatorBetrokkene.vestigingsNummer != null -> {
                    addVestigingInitiatorRole(
                        initiatorBetrokkene.vestigingsNummer,
                        zaak.url,
                        zaak.zaaktype
                    )
                }

                else -> {
                    LOG.warning(
                        "Betrokkene with initiator role in productaanvraag does not contain a BSN or vestigingsnummer. " +
                            "No initiator role created for productaanvraag: '$productaanvraag'."
                    )
                }
            }
        }
    }

    private fun addNatuurlijkPersoonInitiatorRole(bsn: String, zaak: URI, zaaktype: URI) =
        ztcClientService.readRoltype(OmschrijvingGeneriekEnum.INITIATOR, zaaktype).let {
            zrcClientService.createRol(
                RolNatuurlijkPersoon(
                    zaak,
                    it,
                    ROL_TOELICHTING,
                    NatuurlijkPersoon(bsn)
                )
            )
        }

    private fun addVestigingInitiatorRole(vestigingsNummer: String, zaak: URI, zaaktype: URI) =
        ztcClientService.readRoltype(OmschrijvingGeneriekEnum.INITIATOR, zaaktype).let {
            zrcClientService.createRol(
                RolVestiging(
                    zaak,
                    it,
                    ROL_TOELICHTING,
                    Vestiging(vestigingsNummer)
                )
            )
        }

    private fun assignZaak(zaak: Zaak, zaakafhandelParameters: ZaakafhandelParameters) {
        zaakafhandelParameters.groepID?.let {
            LOG.info("Assigning zaak ${zaak.uuid} to group: '${zaakafhandelParameters.groepID}'")
            zrcClientService.createRol(creeerRolGroep(zaakafhandelParameters.groepID, zaak))
        }
        zaakafhandelParameters.gebruikersnaamMedewerker?.let {
            LOG.info("Assigning zaak ${zaak.uuid}: to assignee: '$it'")
            zrcClientService.createRol(creeerRolMedewerker(zaakafhandelParameters.gebruikersnaamMedewerker, zaak))
        }
    }

    private fun creeerRolGroep(groepID: String, zaak: Zaak): RolOrganisatorischeEenheid =
        identityService.readGroup(groepID).let {
            OrganisatorischeEenheid().apply {
                identificatie = it.id
                naam = it.name
            }
        }.let { organistatorischeEenheid ->
            RolOrganisatorischeEenheid(
                zaak.url,
                ztcClientService.readRoltype(OmschrijvingGeneriekEnum.BEHANDELAAR, zaak.zaaktype),
                "Behandelend groep van de zaak",
                organistatorischeEenheid
            )
        }

    private fun creeerRolMedewerker(behandelaarGebruikersnaam: String, zaak: Zaak): RolMedewerker =
        identityService.readUser(behandelaarGebruikersnaam).let {
            Medewerker().apply {
                identificatie = it.id
                voorletters = it.firstName
                achternaam = it.lastName
            }
        }.let { medewerker ->
            RolMedewerker(
                zaak.url,
                ztcClientService.readRoltype(OmschrijvingGeneriekEnum.BEHANDELAAR, zaak.zaaktype),
                "Behandelaar van de zaak",
                medewerker
            )
        }

    private fun deleteInboxDocument(documentUUID: UUID) =
        inboxDocumentenService.find(documentUUID).ifPresent { inboxDocumentenService.delete(it.id) }

    private fun findZaaktypeByIdentificatie(zaaktypeIdentificatie: String) =
        ztcClientService.listZaaktypen(configuratieService.readDefaultCatalogusURI())
            .firstOrNull { it.identificatie == zaaktypeIdentificatie }

    private fun getZaakOmschrijving(productaanvraag: ProductaanvraagDimpact): String =
        "Aangemaakt vanuit ${productaanvraag.bron.naam} met kenmerk '${productaanvraag.bron.kenmerk}'".let {
            return if (it.length > ZAAK_DESCRIPTION_MAX_LENGTH) {
                // we truncate the zaak description to the maximum length allowed by the ZGW ZRC API
                // or else it will not be accepted by the ZGW API implementation component
                LOG.warning(
                    "Truncating zaak description '$it' to the maximum length allowed by the ZGW ZRC API"
                )
                it.substring(0, ZAAK_DESCRIPTION_MAX_LENGTH)
            } else {
                it
            }
        }

    @Suppress("TooGenericExceptionCaught", "NestedBlockDepth")
    private fun handleProductaanvraagDimpact(productaanvraagObject: ORObject) {
        LOG.fine { "Start handling productaanvraag with object URL: ${productaanvraagObject.url}" }
        val productaanvraag = getProductaanvraag(productaanvraagObject)
        val zaaktypeUUID = zaakafhandelParameterBeheerService.findZaaktypeUUIDByProductaanvraagType(
            productaanvraag.type
        )
        if (zaaktypeUUID.isPresent) {
            try {
                zaaktypeUUID.get().let {
                    LOG.fine { "Creating a zaak using a CMMN case with zaaktype: $it" }
                    startZaakWithCmmnProcess(it, productaanvraag, productaanvraagObject)
                }
            } catch (exception: RuntimeException) {
                logZaakCouldNotBeCreatedWarning("CMMN", productaanvraag, exception)
            }
        } else {
            findZaaktypeByIdentificatie(productaanvraag.type)?.let {
                try {
                    LOG.fine { "Creating a zaak using a BPMN proces with zaaktype: $it" }
                    startZaakWithBpmnProcess(it, productaanvraag, productaanvraagObject)
                } catch (exception: RuntimeException) {
                    logZaakCouldNotBeCreatedWarning("BPMN", productaanvraag, exception)
                }
            } ?: run {
                LOG.info("No zaaktype found for productaanvraag-Dimpact type '${productaanvraag.type}'. No zaak was created.")
                registreerInbox(productaanvraag, productaanvraagObject)
            }
        }
    }

    /**
     * Checks if the required attributes defined by the 'Productaanvraag Dimpact' JSON schema are present.
     * This is a bit of a poor man's solution because we are currently 'misusing' the very generic Objects API
     * to store specific productaanvraag JSON data.
     */
    private fun isProductaanvraagDimpact(productaanvraagObject: ORObject) =
        productaanvraagObject.record.data.let {
            it.containsKey(PRODUCTAANVRAAG_FORMULIER_VELD_BRON) &&
                it.containsKey(PRODUCTAANVRAAG_FORMULIER_VELD_TYPE) &&
                it.containsKey(PRODUCTAANVRAAG_FORMULIER_VELD_AANVRAAGGEGEVENS)
        }

    private fun pairProductaanvraagInfoWithZaak(
        productaanvraag: ProductaanvraagDimpact,
        productaanvraagObject: ORObject,
        zaak: Zaak
    ) {
        pairProductaanvraagWithZaak(productaanvraagObject, zaak.url)
        pairAanvraagPDFWithZaak(productaanvraag, zaak.url)
        productaanvraag.bijlagen?.let { pairBijlagenWithZaak(it, zaak.url) }
        addBetrokkenen(productaanvraag, zaak)
    }

    private fun registreerInbox(productaanvraag: ProductaanvraagDimpact, productaanvraagObject: ORObject) {
        val inboxProductaanvraag = InboxProductaanvraag().apply {
            productaanvraagObjectUUID = productaanvraagObject.uuid
            type = productaanvraag.type
            ontvangstdatum = productaanvraagObject.record.registrationAt
        }
        productaanvraag.betrokkenen?.let { betrokkenen ->
            // we are only interested in the first betrokkene with the role 'INITIATOR'
            betrokkenen.first { it.rolOmschrijvingGeneriek == Betrokkene.RolOmschrijvingGeneriek.INITIATOR }
                .let { inboxProductaanvraag.initiatorID = it.inpBsn }
        }
        productaanvraag.pdf?.let { pdfUri ->
            uuidFromURI(pdfUri).let {
                inboxProductaanvraag.aanvraagdocumentUUID = it
                deleteInboxDocument(it)
            }
        }
        productaanvraag.bijlagen?.let { bijlagen ->
            inboxProductaanvraag.aantalBijlagen = bijlagen.size
            bijlagen.forEach { deleteInboxDocument(uuidFromURI(it)) }
        }

        inboxProductaanvraagService.create(inboxProductaanvraag)
    }

    private fun startZaakWithBpmnProcess(
        zaaktype: ZaakType,
        productaanvraag: ProductaanvraagDimpact,
        productaanvraagObject: ORObject
    ) {
        val createdZaak = Zaak().apply {
            this.zaaktype = zaaktype.url
            bronorganisatie = ConfiguratieService.BRON_ORGANISATIE
            verantwoordelijkeOrganisatie = ConfiguratieService.BRON_ORGANISATIE
            startdatum = LocalDate.now()
        }.let(zgwApiService::createZaak)
        bpmnService.readProcessDefinitionByprocessDefinitionKey(BPMN_PROCESS_DEFINITION_KEY).let {
            zrcClientService.createRol(creeerRolGroep(it.description, createdZaak))
        }
        pairProductaanvraagInfoWithZaak(productaanvraag, productaanvraagObject, createdZaak)
        bpmnService.startProcess(
            createdZaak,
            zaaktype,
            getAanvraaggegevens(productaanvraagObject),
            BPMN_PROCESS_DEFINITION_KEY
        )
    }

    private fun logZaakCouldNotBeCreatedWarning(
        processType: String,
        productaanvraag: ProductaanvraagDimpact,
        exception: RuntimeException
    ) {
        LOG.log(
            Level.WARNING,
            "Failed to create a zaak of process type: '$processType' for productaanvraag '${productaanvraag.aanvraaggegevens}'",
            exception
        )
    }
}
