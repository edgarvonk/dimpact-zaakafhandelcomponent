/*
 * SPDX-FileCopyrightText: 2024 Lifely
 * SPDX-License-Identifier: EUPL-1.2+
 */
package net.atos.zac.productaanvraag

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.Runs
import io.mockk.checkUnnecessaryStub
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.slot
import io.mockk.verify
import net.atos.client.or.`object`.ObjectsClientService
import net.atos.client.or.`object`.model.createORObject
import net.atos.client.or.`object`.model.createObjectRecord
import net.atos.client.zgw.drc.DrcClientService
import net.atos.client.zgw.drc.model.createEnkelvoudigInformatieObject
import net.atos.client.zgw.shared.ZGWApiService
import net.atos.client.zgw.zrc.ZrcClientService
import net.atos.client.zgw.zrc.model.BetrokkeneType
import net.atos.client.zgw.zrc.model.Point
import net.atos.client.zgw.zrc.model.Rol
import net.atos.client.zgw.zrc.model.Zaak
import net.atos.client.zgw.zrc.model.ZaakInformatieobject
import net.atos.client.zgw.zrc.model.createZaak
import net.atos.client.zgw.zrc.model.createZaakInformatieobject
import net.atos.client.zgw.zrc.model.createZaakobjectProductaanvraag
import net.atos.client.zgw.ztc.ZtcClientService
import net.atos.client.zgw.ztc.model.createRolType
import net.atos.client.zgw.ztc.model.createZaakType
import net.atos.client.zgw.ztc.model.generated.OmschrijvingGeneriekEnum
import net.atos.zac.admin.ZaakafhandelParameterBeheerService
import net.atos.zac.admin.ZaakafhandelParameterService
import net.atos.zac.admin.model.createZaakafhandelParameters
import net.atos.zac.configuratie.ConfiguratieService
import net.atos.zac.documenten.InboxDocumentenService
import net.atos.zac.flowable.bpmn.BPMNService
import net.atos.zac.flowable.cmmn.CMMNService
import net.atos.zac.identity.IdentityService
import net.atos.zac.productaanvraag.model.InboxProductaanvraag
import net.atos.zac.productaanvraag.model.generated.Geometry
import java.net.URI
import java.util.UUID

@Suppress("LargeClass")
class ProductaanvraagServiceTest : BehaviorSpec({
    val objectsClientService = mockk<ObjectsClientService>()
    val zgwApiService = mockk<ZGWApiService>()
    val zrcClientService = mockk<ZrcClientService>()
    val drcClientService = mockk<DrcClientService>()
    val ztcClientService = mockk<ZtcClientService>()
    val identityService = mockk<IdentityService>()
    val zaakafhandelParameterService = mockk<ZaakafhandelParameterService>()
    val zaakafhandelParameterBeheerService = mockk<ZaakafhandelParameterBeheerService>()
    val inboxDocumentenService = mockk<InboxDocumentenService>()
    val inboxProductaanvraagService = mockk<InboxProductaanvraagService>()
    val cmmnService = mockk<CMMNService>()
    val bpmnService = mockk<BPMNService>()
    val configuratieService = mockk<ConfiguratieService>()
    val productaanvraagService = ProductaanvraagService(
        objectsClientService,
        zgwApiService,
        zrcClientService,
        drcClientService,
        ztcClientService,
        identityService,
        zaakafhandelParameterService,
        zaakafhandelParameterBeheerService,
        inboxDocumentenService,
        inboxProductaanvraagService,
        cmmnService,
        bpmnService,
        configuratieService
    )

    beforeEach {
        checkUnnecessaryStub()
    }

    Given("a productaanvraag-dimpact object with aanvraaggegevens containing form steps with key-value pairs") {
        val type = "productaanvraag"
        val bron = createBron()
        val orObject = createORObject(
            record = createObjectRecord(
                data = mapOf(
                    "bron" to bron,
                    "type" to type,
                    "aanvraaggegevens" to mapOf(
                        "formStep1" to mapOf(
                            "dummyKey1" to "dummyValue1",
                            "dummyKey2" to "dummyValue2"
                        ),
                        "formStep2" to mapOf(
                            "dummyKey3" to "dummyValue3"
                        )
                    )
                )
            )
        )
        When("the form data is requested from the productaanvraag") {
            val formData = productaanvraagService.getAanvraaggegevens(orObject)

            Then("all key-value pairs in the aanvraaggegevens are returned") {
                with(formData) {
                    this["dummyKey1"] shouldBe "dummyValue1"
                    this["dummyKey2"] shouldBe "dummyValue2"
                    this["dummyKey3"] shouldBe "dummyValue3"
                }
            }
        }
    }

    Given("a productaanvraag-dimpact object registration object") {
        val type = "productaanvraag"
        val bron = createBron()
        val zaakIdentificatie = "dummyZaakIdentificatie"
        val coordinates = listOf(52.08968250760225, 5.114358701512936)
        val orObject = createORObject(
            record = createObjectRecord(
                data = mapOf(
                    "bron" to bron,
                    "type" to type,
                    "zaakgegevens" to mapOf(
                        "identificatie" to zaakIdentificatie,
                        "geometry" to mapOf(
                            "type" to "Point",
                            "coordinates" to coordinates
                        )
                    )
                )
            )
        )
        When("the productaanvraag is requested from the product aanvraag service") {
            val productAanVraagDimpact = productaanvraagService.getProductaanvraag(orObject)

            Then("the productaanvraag of type 'productaanvraag Dimpact' is returned and contains the expected data") {
                with(productAanVraagDimpact) {
                    with(this.bron) {
                        naam shouldBe bron.naam
                        kenmerk shouldBe bron.kenmerk
                    }
                    taal shouldBe "nld"
                    type shouldBe type
                    with(zaakgegevens) {
                        identificatie shouldBe zaakIdentificatie
                        with(geometry) {
                            this.type shouldBe Geometry.Type.POINT
                            this.coordinates shouldBe coordinates
                        }
                    }
                }
            }
        }
    }
    Given("a productaanvraag-dimpact object registration object without zaakgegevens") {
        val type = "productaanvraag"
        val bron = createBron()
        val orObject = createORObject(
            record = createObjectRecord(
                data = mapOf(
                    "bron" to bron,
                    "type" to type
                )
            )
        )
        When("the productaanvraag is requested from the product aanvraag service") {
            val productAanVraagDimpact = productaanvraagService.getProductaanvraag(orObject)

            Then("the productaanvraag of type 'productaanvraag Dimpact' is returned and contains the expected data") {
                with(productAanVraagDimpact) {
                    with(this.bron) {
                        naam shouldBe bron.naam
                        kenmerk shouldBe bron.kenmerk
                    }
                    taal shouldBe "nld"
                    type shouldBe type
                    zaakgegevens shouldBe null
                }
            }
        }
    }
    Given(
        """
        a productaanvraag-dimpact object registration object containing zaakgegevens with a point geometry and 
        a betrokkene with role initiator and type BSN as well as a betrokkene with role initiator and type vestiging
        """
    ) {
        val productAanvraagObjectUUID = UUID.randomUUID()
        val zaakTypeUUID = UUID.randomUUID()
        val productAanvraagType = "productaanvraag"
        val zaakType = createZaakType()
        val createdZaak = createZaak()
        val createdZaakobjectProductAanvraag = createZaakobjectProductaanvraag()
        val createdZaakInformatieobject = createZaakInformatieobject()
        val zaakafhandelParameters = createZaakafhandelParameters(
            zaaktypeUUID = zaakTypeUUID,
        )
        val formulierBron = createBron()
        val coordinates = listOf(52.08968250760225, 5.114358701512936)
        val bsnNumber = "dummyBsnNumber"
        val productAanvraagORObject = createORObject(
            record = createObjectRecord(
                data = mapOf(
                    "bron" to formulierBron,
                    "type" to productAanvraagType,
                    // aanvraaggegevens must contain at least one key with a map value
                    "aanvraaggegevens" to mapOf("dummyKey" to mapOf("dummySubKey" to "dummyValue")),
                    "zaakgegevens" to mapOf(
                        "geometry" to mapOf(
                            "type" to "Point",
                            "coordinates" to coordinates
                        )
                    ),
                    "betrokkenen" to listOf(
                        mapOf(
                            "inpBsn" to bsnNumber,
                            "rolOmschrijvingGeneriek" to "initiator"
                        ),
                        mapOf(
                            "vestigingsNummer" to "dummyVestigingsNummer",
                            "rolOmschrijvingGeneriek" to "initiator"
                        )
                    )
                )
            )
        )
        val rolTypeInitiator = createRolType(
            zaakTypeUri = zaakType.url,
            omschrijvingGeneriek = OmschrijvingGeneriekEnum.INITIATOR
        )
        val zaakToBeCreated = slot<Zaak>()
        val roleToBeCreated = slot<Rol<*>>()
        every { objectsClientService.readObject(productAanvraagObjectUUID) } returns productAanvraagORObject
        every {
            zaakafhandelParameterBeheerService.findActiveZaakafhandelparametersByProductaanvraagtype(productAanvraagType)
        } returns listOf(zaakafhandelParameters)
        every { ztcClientService.readZaaktype(zaakTypeUUID) } returns zaakType
        every { zgwApiService.createZaak(capture(zaakToBeCreated)) } returns createdZaak
        every { zaakafhandelParameterService.readZaakafhandelParameters(zaakTypeUUID) } returns zaakafhandelParameters
        every { zrcClientService.createZaakobject(any()) } returns createdZaakobjectProductAanvraag
        every {
            zrcClientService.createZaakInformatieobject(
                any(),
                "Document toegevoegd tijdens het starten van de van de zaak vanuit een product aanvraag"
            )
        } returns createdZaakInformatieobject
        every { cmmnService.startCase(createdZaak, zaakType, zaakafhandelParameters, any()) } just Runs
        every { ztcClientService.findRoltypen(any(), OmschrijvingGeneriekEnum.INITIATOR) } returns listOf(rolTypeInitiator)
        every { zrcClientService.createRol(capture(roleToBeCreated)) } just runs

        When("the productaanvraag is handled") {
            productaanvraagService.handleProductaanvraag(productAanvraagObjectUUID)

            Then(
                """
                    a zaak should be created, an initiator role of type 'natuurlijk persoon' should be created for the zaak
                    and a CMMN case process should be started
                    """
            ) {
                verify(exactly = 1) {
                    zgwApiService.createZaak(any())
                    zrcClientService.createZaakobject(any())
                    cmmnService.startCase(createdZaak, zaakType, zaakafhandelParameters, any())
                }
                with(zaakToBeCreated.captured) {
                    zaaktype shouldBe zaakType.url
                    communicatiekanaalNaam shouldBe "E-formulier"
                    bronorganisatie shouldBe "123443210"
                    omschrijving shouldBe "Aangemaakt vanuit ${formulierBron.naam} met kenmerk '${formulierBron.kenmerk}'"
                    toelichting shouldBe null
                    with(zaakgeometrie) {
                        type.toValue() shouldBe Geometry.Type.POINT.value()
                        with((this as Point).coordinates) {
                            latitude.toDouble() shouldBe coordinates[0]
                            longitude.toDouble() shouldBe coordinates[1]
                        }
                    }
                }
                with(roleToBeCreated.captured) {
                    betrokkeneType shouldBe BetrokkeneType.NATUURLIJK_PERSOON
                    identificatienummer shouldBe bsnNumber
                    roltype shouldBe rolTypeInitiator.url
                    zaak shouldBe createdZaak.url
                }
            }
        }
    }
    Given(
        """
        a productaanvraag-dimpact object registration object containing a betrokkene with role initiator and type vestiging
        """
    ) {
        val productAanvraagObjectUUID = UUID.randomUUID()
        val zaakTypeUUID = UUID.randomUUID()
        val productAanvraagType = "productaanvraag"
        val zaakType = createZaakType()
        val createdZaak = createZaak()
        val createdZaakobjectProductAanvraag = createZaakobjectProductaanvraag()
        val createdZaakInformatieobject = createZaakInformatieobject()
        val zaakafhandelParameters = createZaakafhandelParameters(
            zaaktypeUUID = zaakTypeUUID,
        )
        val formulierBron = createBron()
        val vestigingsNummer = "dummyVestigingsNummer"
        val productAanvraagORObject = createORObject(
            record = createObjectRecord(
                data = mapOf(
                    "bron" to formulierBron,
                    "type" to productAanvraagType,
                    // aanvraaggegevens must contain at least one key with a map value
                    "aanvraaggegevens" to mapOf("dummyKey" to mapOf("dummySubKey" to "dummyValue")),
                    "betrokkenen" to listOf(
                        mapOf(
                            "vestigingsNummer" to vestigingsNummer,
                            "rolOmschrijvingGeneriek" to "initiator"
                        )
                    )
                )
            )
        )
        val rolType = createRolType(
            zaakTypeUri = zaakType.url,
            omschrijvingGeneriek = OmschrijvingGeneriekEnum.INITIATOR
        )
        val zaakToBeCreated = slot<Zaak>()
        val roleToBeCreated = slot<Rol<*>>()
        every { objectsClientService.readObject(productAanvraagObjectUUID) } returns productAanvraagORObject
        every {
            zaakafhandelParameterBeheerService.findActiveZaakafhandelparametersByProductaanvraagtype(productAanvraagType)
        } returns listOf(zaakafhandelParameters)
        every { ztcClientService.readZaaktype(zaakTypeUUID) } returns zaakType
        every { zgwApiService.createZaak(capture(zaakToBeCreated)) } returns createdZaak
        every { zaakafhandelParameterService.readZaakafhandelParameters(zaakTypeUUID) } returns zaakafhandelParameters
        every { zrcClientService.createZaakobject(any()) } returns createdZaakobjectProductAanvraag
        every {
            zrcClientService.createZaakInformatieobject(
                any(),
                "Document toegevoegd tijdens het starten van de van de zaak vanuit een product aanvraag"
            )
        } returns createdZaakInformatieobject
        every { cmmnService.startCase(createdZaak, zaakType, zaakafhandelParameters, any()) } just Runs
        every { ztcClientService.findRoltypen(any(), OmschrijvingGeneriekEnum.INITIATOR) } returns listOf(rolType)
        every { zrcClientService.createRol(capture(roleToBeCreated)) } just runs

        When("the productaanvraag is handled") {
            productaanvraagService.handleProductaanvraag(productAanvraagObjectUUID)

            Then(
                """
                    a zaak should be created, an initiator role of type 'vestiging' should be created for the zaak
                    and a CMMN case process should be started
                    """
            ) {
                verify(exactly = 1) {
                    zgwApiService.createZaak(any())
                    zrcClientService.createZaakobject(any())
                    cmmnService.startCase(createdZaak, zaakType, zaakafhandelParameters, any())
                }
                with(zaakToBeCreated.captured) {
                    zaaktype shouldBe zaakType.url
                    communicatiekanaalNaam shouldBe "E-formulier"
                    bronorganisatie shouldBe "123443210"
                    omschrijving shouldBe "Aangemaakt vanuit ${formulierBron.naam} met kenmerk '${formulierBron.kenmerk}'"
                    toelichting shouldBe null
                }
                with(roleToBeCreated.captured) {
                    betrokkeneType shouldBe BetrokkeneType.VESTIGING
                    identificatienummer shouldBe vestigingsNummer
                    roltype shouldBe rolType.url
                    zaak shouldBe createdZaak.url
                }
            }
        }
    }
    Given(
        """
        a productaanvraag-dimpact object registration object containing a betrokkene with role initiator 
        but no supported initiator identification
        """
    ) {
        val productAanvraagObjectUUID = UUID.randomUUID()
        val zaakTypeUUID = UUID.randomUUID()
        val productAanvraagType = "productaanvraag"
        val zaakType = createZaakType()
        val createdZaak = createZaak()
        val createdZaakobjectProductAanvraag = createZaakobjectProductaanvraag()
        val createdZaakInformatieobject = createZaakInformatieobject()
        val zaakafhandelParameters = createZaakafhandelParameters(
            zaaktypeUUID = zaakTypeUUID
        )
        val formulierBron = createBron()
        val productAanvraagORObject = createORObject(
            record = createObjectRecord(
                data = mapOf(
                    "bron" to formulierBron,
                    "type" to productAanvraagType,
                    // aanvraaggegevens must contain at least one key with a map value
                    "aanvraaggegevens" to mapOf("dummyKey" to mapOf("dummySubKey" to "dummyValue")),
                    "betrokkenen" to listOf(
                        mapOf(
                            "unsupportedIdentification" to "1234",
                            "rolOmschrijvingGeneriek" to "initiator"
                        )
                    )
                )
            )
        )
        val zaakToBeCreated = slot<Zaak>()
        every { objectsClientService.readObject(productAanvraagObjectUUID) } returns productAanvraagORObject
        every {
            zaakafhandelParameterBeheerService.findActiveZaakafhandelparametersByProductaanvraagtype(productAanvraagType)
        } returns listOf(zaakafhandelParameters)
        every { ztcClientService.readZaaktype(zaakTypeUUID) } returns zaakType
        every { zgwApiService.createZaak(capture(zaakToBeCreated)) } returns createdZaak
        every { zaakafhandelParameterService.readZaakafhandelParameters(zaakTypeUUID) } returns zaakafhandelParameters
        every { zrcClientService.createZaakobject(any()) } returns createdZaakobjectProductAanvraag
        every {
            zrcClientService.createZaakInformatieobject(
                any(),
                "Document toegevoegd tijdens het starten van de van de zaak vanuit een product aanvraag"
            )
        } returns createdZaakInformatieobject
        every { cmmnService.startCase(createdZaak, zaakType, zaakafhandelParameters, any()) } just Runs
        every { ztcClientService.findRoltypen(any(), OmschrijvingGeneriekEnum.INITIATOR) } returns emptyList()

        When("the productaanvraag is handled") {
            productaanvraagService.handleProductaanvraag(productAanvraagObjectUUID)

            Then(
                """
                    a zaak should be created, no initiator role should be created for the zaak
                    and a CMMN case process should be started
                    """
            ) {
                verify(exactly = 1) {
                    zgwApiService.createZaak(any())
                    zrcClientService.createZaakobject(any())
                    cmmnService.startCase(createdZaak, zaakType, zaakafhandelParameters, any())
                }
                verify(exactly = 0) {
                    zrcClientService.createRol(any())
                }
                with(zaakToBeCreated.captured) {
                    zaaktype shouldBe zaakType.url
                    communicatiekanaalNaam shouldBe "E-formulier"
                    bronorganisatie shouldBe "123443210"
                    omschrijving shouldBe "Aangemaakt vanuit ${formulierBron.naam} met kenmerk '${formulierBron.kenmerk}'"
                    toelichting shouldBe null
                }
            }
        }
    }
    Given(
        """
        a productaanvraag-dimpact object registration object not containing any betrokkenen
        """
    ) {
        val productAanvraagObjectUUID = UUID.randomUUID()
        val zaakTypeUUID = UUID.randomUUID()
        val productAanvraagType = "productaanvraag"
        val zaakType = createZaakType()
        val createdZaak = createZaak()
        val createdZaakobjectProductAanvraag = createZaakobjectProductaanvraag()
        val createdZaakInformatieobject = createZaakInformatieobject()
        val zaakafhandelParameters = createZaakafhandelParameters(
            zaaktypeUUID = zaakTypeUUID
        )
        val formulierBron = createBron()
        val productAanvraagORObject = createORObject(
            record = createObjectRecord(
                data = mapOf(
                    "bron" to formulierBron,
                    "type" to productAanvraagType,
                    // aanvraaggegevens must contain at least one key with a map value
                    "aanvraaggegevens" to mapOf("dummyKey" to mapOf("dummySubKey" to "dummyValue"))
                )
            )
        )
        val zaakToBeCreated = slot<Zaak>()
        every { objectsClientService.readObject(productAanvraagObjectUUID) } returns productAanvraagORObject
        every {
            zaakafhandelParameterBeheerService.findActiveZaakafhandelparametersByProductaanvraagtype(productAanvraagType)
        } returns listOf(zaakafhandelParameters)
        every { ztcClientService.readZaaktype(zaakTypeUUID) } returns zaakType
        every { zgwApiService.createZaak(capture(zaakToBeCreated)) } returns createdZaak
        every { zaakafhandelParameterService.readZaakafhandelParameters(zaakTypeUUID) } returns zaakafhandelParameters
        every { zrcClientService.createZaakobject(any()) } returns createdZaakobjectProductAanvraag
        every {
            zrcClientService.createZaakInformatieobject(
                any(),
                "Document toegevoegd tijdens het starten van de van de zaak vanuit een product aanvraag"
            )
        } returns createdZaakInformatieobject
        every { cmmnService.startCase(createdZaak, zaakType, zaakafhandelParameters, any()) } just Runs

        When("the productaanvraag is handled") {
            productaanvraagService.handleProductaanvraag(productAanvraagObjectUUID)

            Then(
                """
                    a zaak should be created and a CMMN case process should be started
                    """
            ) {
                verify(exactly = 1) {
                    zgwApiService.createZaak(any())
                    zrcClientService.createZaakobject(any())
                    cmmnService.startCase(createdZaak, zaakType, zaakafhandelParameters, any())
                }
                verify(exactly = 0) {
                    zrcClientService.createRol(any())
                }
                with(zaakToBeCreated.captured) {
                    zaaktype shouldBe zaakType.url
                    communicatiekanaalNaam shouldBe "E-formulier"
                    bronorganisatie shouldBe "123443210"
                    omschrijving shouldBe "Aangemaakt vanuit ${formulierBron.naam} met kenmerk '${formulierBron.kenmerk}'"
                    toelichting shouldBe null
                }
            }
        }
    }
    given(
        """
        a productaanvraag-dimpact object registration object containing a list of supported betrokkenen
        including behandelaar but no initiator
        """
    ) {
        val productAanvraagObjectUUID = UUID.randomUUID()
        val zaakTypeUUID = UUID.randomUUID()
        val productAanvraagType = "productaanvraag"
        val zaakType = createZaakType()
        val createdZaak = createZaak()
        val createdZaakobjectProductAanvraag = createZaakobjectProductaanvraag()
        val createdZaakInformatieobject = createZaakInformatieobject()
        val zaakafhandelParameters = createZaakafhandelParameters(
            zaaktypeUUID = zaakTypeUUID
        )
        val formulierBron = createBron()
        val adviseurBsn1 = "dummyBsn1"
        val behandelaarBsn = "dummyBsn3"
        val beslisserBsn = "dummyBsn4"
        val klantcontacterBsn = "dummyBsn5"
        val medeInitiatorBsn = "dummyBsn6"
        val belanghebbendeVestigingsnummer1 = "dummyVestigingsNummer1"
        val belanghebbendeVestigingsnummer2 = "dummyVestigingsNummer2"
        val beslisserVestigingsnummer = "dummyVestigingsNummer3"
        val zaakcoordinatorVestigingsnummer = "dummyVestigingsNummer4"
        val rolTypeBelanghebbende = createRolType(
            zaakTypeUri = zaakType.url,
            omschrijvingGeneriek = OmschrijvingGeneriekEnum.BELANGHEBBENDE
        )
        val rolTypeBeslisser = createRolType(
            zaakTypeUri = zaakType.url,
            omschrijvingGeneriek = OmschrijvingGeneriekEnum.BESLISSER
        )
        val rolTypeKlantcontacter1 = createRolType(
            zaakTypeUri = zaakType.url,
            omschrijvingGeneriek = OmschrijvingGeneriekEnum.KLANTCONTACTER
        )
        val rolTypeKlantcontacter2 = createRolType(
            zaakTypeUri = zaakType.url,
            omschrijvingGeneriek = OmschrijvingGeneriekEnum.KLANTCONTACTER
        )
        val rolTypeMedeInitiator = createRolType(
            zaakTypeUri = zaakType.url,
            omschrijvingGeneriek = OmschrijvingGeneriekEnum.MEDE_INITIATOR
        )
        val rolTypeZaakcoordinator = createRolType(
            zaakTypeUri = zaakType.url,
            omschrijvingGeneriek = OmschrijvingGeneriekEnum.ZAAKCOORDINATOR
        )
        val productAanvraagORObject = createORObject(
            record = createObjectRecord(
                data = mapOf(
                    "bron" to formulierBron,
                    "type" to productAanvraagType,
                    // aanvraaggegevens must contain at least one key with a map value
                    "aanvraaggegevens" to mapOf("dummyKey" to mapOf("dummySubKey" to "dummyValue")),
                    "betrokkenen" to listOf(
                        mapOf(
                            "inpBsn" to adviseurBsn1,
                            "rolOmschrijvingGeneriek" to "adviseur"
                        ),
                        mapOf(
                            "inpBsn" to behandelaarBsn,
                            "rolOmschrijvingGeneriek" to "behandelaar"
                        ),
                        mapOf(
                            "vestigingsNummer" to belanghebbendeVestigingsnummer1,
                            "rolOmschrijvingGeneriek" to "belanghebbende"
                        ),
                        mapOf(
                            "vestigingsNummer" to belanghebbendeVestigingsnummer2,
                            "rolOmschrijvingGeneriek" to "belanghebbende"
                        ),
                        mapOf(
                            "inpBsn" to beslisserBsn,
                            "rolOmschrijvingGeneriek" to "beslisser"
                        ),
                        mapOf(
                            "vestigingsNummer" to beslisserVestigingsnummer,
                            "rolOmschrijvingGeneriek" to "beslisser"
                        ),
                        mapOf(
                            "inpBsn" to klantcontacterBsn,
                            "rolOmschrijvingGeneriek" to "klantcontacter"
                        ),
                        mapOf(
                            "inpBsn" to medeInitiatorBsn,
                            "rolOmschrijvingGeneriek" to "mede_initiator"
                        ),
                        mapOf(
                            "vestigingsNummer" to zaakcoordinatorVestigingsnummer,
                            "rolOmschrijvingGeneriek" to "zaakcoordinator"
                        )
                    )
                )
            )
        )
        val rolesToBeCreated = mutableListOf<Rol<*>>()
        val zaakToBeCreated = slot<Zaak>()
        every { objectsClientService.readObject(productAanvraagObjectUUID) } returns productAanvraagORObject
        every {
            zaakafhandelParameterBeheerService.findActiveZaakafhandelparametersByProductaanvraagtype(productAanvraagType)
        } returns listOf(zaakafhandelParameters)
        every { ztcClientService.readZaaktype(zaakTypeUUID) } returns zaakType
        // here we simulate the case that no role types have been defined for the adviseur role
        every { ztcClientService.findRoltypen(any(), OmschrijvingGeneriekEnum.ADVISEUR) } returns emptyList()
        every {
            ztcClientService.findRoltypen(any(), OmschrijvingGeneriekEnum.BELANGHEBBENDE)
        } returns listOf(rolTypeBelanghebbende)
        every { ztcClientService.findRoltypen(any(), OmschrijvingGeneriekEnum.BESLISSER) } returns listOf(rolTypeBeslisser)
        // here we simulate the case that multiple role types have been defined for the klantcontacter role
        every {
            ztcClientService.findRoltypen(any(), OmschrijvingGeneriekEnum.KLANTCONTACTER)
        } returns listOf(rolTypeKlantcontacter1, rolTypeKlantcontacter2)
        every {
            ztcClientService.findRoltypen(any(), OmschrijvingGeneriekEnum.MEDE_INITIATOR)
        } returns listOf(rolTypeMedeInitiator)
        every {
            ztcClientService.findRoltypen(any(), OmschrijvingGeneriekEnum.ZAAKCOORDINATOR)
        } returns listOf(rolTypeZaakcoordinator)
        every { zrcClientService.createRol(capture(rolesToBeCreated)) } just runs
        every { zgwApiService.createZaak(capture(zaakToBeCreated)) } returns createdZaak
        every { zaakafhandelParameterService.readZaakafhandelParameters(zaakTypeUUID) } returns zaakafhandelParameters
        every { zrcClientService.createZaakobject(any()) } returns createdZaakobjectProductAanvraag
        every {
            zrcClientService.createZaakInformatieobject(
                any(),
                "Document toegevoegd tijdens het starten van de van de zaak vanuit een product aanvraag"
            )
        } returns createdZaakInformatieobject
        every { cmmnService.startCase(createdZaak, zaakType, zaakafhandelParameters, any()) } just Runs

        When("the productaanvraag is handled") {
            productaanvraagService.handleProductaanvraag(productAanvraagObjectUUID)

            Then(
                """
                    a zaak should be created, roles should be created for all supported betrokkenen types for which
                    there are role types defined in the ZTC client service,
                    except for the behandelaar betrokkene, and a CMMN case process should be started
                    """
            ) {
                verify(exactly = 1) {
                    zgwApiService.createZaak(any())
                    zrcClientService.createZaakobject(any())
                    cmmnService.startCase(createdZaak, zaakType, zaakafhandelParameters, any())
                }
                verify(exactly = 7) {
                    zrcClientService.createRol(any())
                }
                with(zaakToBeCreated.captured) {
                    zaaktype shouldBe zaakType.url
                    communicatiekanaalNaam shouldBe "E-formulier"
                    bronorganisatie shouldBe "123443210"
                    omschrijving shouldBe "Aangemaakt vanuit ${formulierBron.naam} met kenmerk '${formulierBron.kenmerk}'"
                    toelichting shouldBe null
                }
                rolesToBeCreated.forEach {
                    it.roltoelichting shouldBe "Overgenomen vanuit de product aanvraag"
                    it.zaak shouldBe createdZaak.url
                }
                with(rolesToBeCreated[0]) {
                    betrokkeneType shouldBe BetrokkeneType.VESTIGING
                    identificatienummer shouldBe belanghebbendeVestigingsnummer1
                    roltype shouldBe rolTypeBelanghebbende.url
                }
                with(rolesToBeCreated[1]) {
                    betrokkeneType shouldBe BetrokkeneType.VESTIGING
                    identificatienummer shouldBe belanghebbendeVestigingsnummer2
                    roltype shouldBe rolTypeBelanghebbende.url
                }
                with(rolesToBeCreated[2]) {
                    betrokkeneType shouldBe BetrokkeneType.NATUURLIJK_PERSOON
                    identificatienummer shouldBe beslisserBsn
                    roltype shouldBe rolTypeBeslisser.url
                }
                with(rolesToBeCreated[3]) {
                    betrokkeneType shouldBe BetrokkeneType.VESTIGING
                    identificatienummer shouldBe beslisserVestigingsnummer
                    roltype shouldBe rolTypeBeslisser.url
                }
                with(rolesToBeCreated[4]) {
                    betrokkeneType shouldBe BetrokkeneType.NATUURLIJK_PERSOON
                    identificatienummer shouldBe klantcontacterBsn
                    roltype shouldBe rolTypeKlantcontacter1.url
                }
                with(rolesToBeCreated[5]) {
                    betrokkeneType shouldBe BetrokkeneType.NATUURLIJK_PERSOON
                    identificatienummer shouldBe medeInitiatorBsn
                    roltype shouldBe rolTypeMedeInitiator.url
                }
                with(rolesToBeCreated[6]) {
                    betrokkeneType shouldBe BetrokkeneType.VESTIGING
                    identificatienummer shouldBe zaakcoordinatorVestigingsnummer
                    roltype shouldBe rolTypeZaakcoordinator.url
                }
            }
        }
    }
    Given("a productaanvraag-dimpact object registration object missing required aanvraaggegevens") {
        val productAanvraagObjectUUID = UUID.randomUUID()
        val productAanvraagType = "productaanvraag"
        val formulierBron = createBron()
        val productAanvraagORObjectWithMissingAanvraaggegevens = createORObject(
            record = createObjectRecord(
                data = mapOf(
                    "bron" to formulierBron,
                    "type" to productAanvraagType
                )
            )
        )
        every {
            objectsClientService.readObject(productAanvraagObjectUUID)
        } returns productAanvraagORObjectWithMissingAanvraaggegevens

        When("the productaanvraag is handled") {
            productaanvraagService.handleProductaanvraag(productAanvraagObjectUUID)

            Then(
                """
                an inbox productaanvraag should be created and a zaak should not be created, 
                and a CMMN case process should not be started
                """
            ) {
                verify(exactly = 0) {
                    zgwApiService.createZaak(any())
                    zrcClientService.createZaakobject(any())
                    cmmnService.startCase(any(), any(), any(), any())
                }
            }
        }
    }
    Given(
        """
        a productaanvraag-dimpact object registration object containing required data but
        no betrokkenen and which contains a zaaktype for which no zaakafhandel parameters are configured 
        and for which no zaaktype exists in the ZTC catalogus
        """
    ) {
        val productAanvraagObjectUUID = UUID.randomUUID()
        val catalogusURI = URI("dummyCatalogusURI")
        val productAanvraagType = "productaanvraag"
        val zaakType = createZaakType()
        val createdZaak = createZaak()
        val zaakafhandelParameters = createZaakafhandelParameters()
        val formulierBron = createBron()
        val productAanvraagORObject = createORObject(
            record = createObjectRecord(
                data = mapOf(
                    "bron" to formulierBron,
                    "type" to productAanvraagType,
                    // aanvraaggegevens must contain at least one key with a map value
                    "aanvraaggegevens" to mapOf("dummyKey" to mapOf("dummySubKey" to "dummyValue"))
                )
            ),
            uuid = productAanvraagObjectUUID
        )
        val inboxProductaanvraagSlot = slot<InboxProductaanvraag>()
        every { objectsClientService.readObject(productAanvraagObjectUUID) } returns productAanvraagORObject
        // no zaakafhandelparameters are configured for the zaaktype
        every {
            zaakafhandelParameterBeheerService.findActiveZaakafhandelparametersByProductaanvraagtype(productAanvraagType)
        } returns emptyList()
        every { configuratieService.readDefaultCatalogusURI() } returns catalogusURI
        every { ztcClientService.listZaaktypen(catalogusURI) } returns listOf(zaakType)
        every { inboxProductaanvraagService.create(capture(inboxProductaanvraagSlot)) } just runs

        When("the productaanvraag is handled") {
            productaanvraagService.handleProductaanvraag(productAanvraagObjectUUID)

            Then(
                """
                an inbox productaanvraag should be created, no zaak should be created and no CMMN case process should be started
                """
            ) {
                verify(exactly = 1) {
                    inboxProductaanvraagService.create(any())
                }
                verify(exactly = 0) {
                    zgwApiService.createZaak(any())
                    zrcClientService.createZaakobject(any())
                    cmmnService.startCase(createdZaak, zaakType, zaakafhandelParameters, any())
                }
                inboxProductaanvraagSlot.captured.run {
                    productaanvraagObjectUUID shouldBe productAanvraagObjectUUID
                    aanvraagdocumentUUID shouldBe null
                    ontvangstdatum shouldBe null
                    type shouldBe productAanvraagType
                    initiatorID shouldBe null
                    aantalBijlagen shouldBe 0
                }
            }
        }
    }
    Given("a list of bijlage URIs and a zaak URI") {
        val bijlageURIs = listOf(URI("dummyURI1"), URI("dummyURI2"))
        val enkelvoudigInformatieobjecten = listOf(
            createEnkelvoudigInformatieObject(),
            createEnkelvoudigInformatieObject()
        )
        val zaakInformatieobjecten = listOf(createZaakInformatieobject(), createZaakInformatieobject())
        val zaakUrl = URI("dummyZaakUrl")
        val createdZaakInformatieobjectSlot = slot<ZaakInformatieobject>()
        val beschrijving = "Document toegevoegd tijdens het starten van de van de zaak vanuit een product aanvraag"
        bijlageURIs.forEachIndexed { index, uri ->
            every { drcClientService.readEnkelvoudigInformatieobject(uri) } returns enkelvoudigInformatieobjecten[index]
            every { drcClientService.readEnkelvoudigInformatieobject(uri) } returns enkelvoudigInformatieobjecten[index]
        }
        every {
            zrcClientService.createZaakInformatieobject(
                capture(createdZaakInformatieobjectSlot),
                beschrijving
            )
        } returns zaakInformatieobjecten[0] andThenAnswer { zaakInformatieobjecten[1] }

        When("the bijlagen are paired with the zaak") {
            productaanvraagService.pairBijlagenWithZaak(bijlageURIs, zaakUrl)

            Then("for every bijlage a zaakInformatieobject should be created") {
                verify(exactly = 2) {
                    zrcClientService.createZaakInformatieobject(any(), any())
                }
                createdZaakInformatieobjectSlot.captured.run {
                    zaak shouldBe zaakUrl
                    beschrijving shouldBe beschrijving
                    informatieobject shouldBe enkelvoudigInformatieobjecten[1].url
                    titel shouldBe enkelvoudigInformatieobjecten[1].titel
                }
            }
        }
    }
})
