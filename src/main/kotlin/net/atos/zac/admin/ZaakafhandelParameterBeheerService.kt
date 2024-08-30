/*
 * SPDX-FileCopyrightText: 2021 Atos, 2024 Lifely
 * SPDX-License-Identifier: EUPL-1.2+
 */
package net.atos.zac.admin

import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import jakarta.transaction.Transactional
import net.atos.client.zgw.ztc.ZtcClientService
import net.atos.client.zgw.ztc.model.generated.ResultaatType
import net.atos.client.zgw.ztc.model.generated.ZaakType
import net.atos.zac.admin.model.HumanTaskParameters
import net.atos.zac.admin.model.MailtemplateKoppeling
import net.atos.zac.admin.model.UserEventListenerParameters
import net.atos.zac.admin.model.ZaakafhandelParameters
import net.atos.zac.admin.model.ZaakbeeindigParameter
import net.atos.zac.admin.model.ZaakbeeindigReden
import net.atos.zac.util.UriUtil
import net.atos.zac.util.ValidationUtil
import nl.lifely.zac.util.AllOpen
import nl.lifely.zac.util.NoArgConstructor
import java.net.URI
import java.time.ZonedDateTime
import java.util.Optional
import java.util.UUID
import java.util.logging.Logger

@ApplicationScoped
@Transactional
@AllOpen
@NoArgConstructor
@Suppress("TooManyFunctions")
open class ZaakafhandelParameterBeheerService @Inject constructor(
    @PersistenceContext(unitName = "ZaakafhandelcomponentPU")
    private val entityManager: EntityManager,
    private val ztcClientService: ZtcClientService,
    private val zaakafhandelParameterService: ZaakafhandelParameterService
) {
    companion object {
        private val LOG: Logger = Logger.getLogger(ZaakafhandelParameterBeheerService::class.java.name)
    }

    fun readZaakafhandelParameters(zaaktypeUUID: UUID): ZaakafhandelParameters {
        ztcClientService.readCacheTime()
        val builder = entityManager.criteriaBuilder
        val query = builder.createQuery(
            ZaakafhandelParameters::class.java
        )
        val root = query.from(
            ZaakafhandelParameters::class.java
        )
        query.select(root).where(builder.equal(root.get<Any>(ZaakafhandelParameters.ZAAKTYPE_UUID), zaaktypeUUID))
        val resultList = entityManager.createQuery(query).resultList
        if (resultList.isNotEmpty()) {
            return resultList.first()
        } else {
            return ZaakafhandelParameters().apply {
                zaakTypeUUID = zaaktypeUUID
            }
        }
    }

    fun listZaakafhandelParameters(): List<ZaakafhandelParameters> {
        val query = entityManager.criteriaBuilder.createQuery(ZaakafhandelParameters::class.java)
        val root = query.from(ZaakafhandelParameters::class.java)
        query.select(root).orderBy(entityManager.criteriaBuilder.desc(root.get<Any>("id")))
        return entityManager.createQuery(query).resultList
    }

    fun createZaakafhandelParameters(zaakafhandelParameters: ZaakafhandelParameters): ZaakafhandelParameters {
        zaakafhandelParameterService.clearListCache()
        ValidationUtil.valideerObject(zaakafhandelParameters)
        zaakafhandelParameters.humanTaskParametersCollection.forEach { ValidationUtil.valideerObject(it) }
        zaakafhandelParameters.userEventListenerParametersCollection.forEach { ValidationUtil.valideerObject(it) }
        zaakafhandelParameters.mailtemplateKoppelingen.forEach { ValidationUtil.valideerObject(it) }
        zaakafhandelParameters.creatiedatum = ZonedDateTime.now()
        entityManager.persist(zaakafhandelParameters)
        return zaakafhandelParameters
    }

    fun updateZaakafhandelParameters(zaakafhandelParameters: ZaakafhandelParameters): ZaakafhandelParameters {
        ValidationUtil.valideerObject(zaakafhandelParameters)
        zaakafhandelParameters.humanTaskParametersCollection.forEach { ValidationUtil.valideerObject(it) }
        zaakafhandelParameters.creatiedatum =
            entityManager.find(ZaakafhandelParameters::class.java, zaakafhandelParameters.id).creatiedatum
        return entityManager.merge(zaakafhandelParameters)
    }

    fun findActiveZaaktypeUuidByProductaanvraagType(productaanvraagType: String?): Optional<UUID> {
        val builder = entityManager.criteriaBuilder
        val query = builder.createQuery(UUID::class.java)
        val root = query.from(ZaakafhandelParameters::class.java)
        query.select(root.get(ZaakafhandelParameters.ZAAKTYPE_UUID))
            .where(builder.equal(root.get<Any>(ZaakafhandelParameters.PRODUCTAANVRAAGTYPE), productaanvraagType))
        query.orderBy(builder.desc(root.get<Any>(ZaakafhandelParameters.CREATIEDATUM)))
        val resultList = entityManager.createQuery(query).resultList
        if (resultList.isNotEmpty()) {
            if (resultList.size > 1) {
                // since we sort on creation date, the first result is by definition the currently active zaakafhandelparameters
                // for a specific zaaktype; all other results (if any) are older inactive versions
                // but it's a different story when we have multiple results for different zaak types (zaak type descriptions)
                // that could happen when the same productaanvraag type is used for multiple zaak types
                // we need to handle that differently
                LOG.fine(
                    String.format(
                        "Multiple zaakafhandelparameters have been found for productaanvraag type: '%s'. " +
                            "Returning the first result with the most recent creation date, with zaaktype UUID: '%s'.",
                        productaanvraagType,
                        resultList.first()
                    )
                )
            }
            return Optional.of<UUID>(resultList.first())
        }
        return Optional.empty()
    }

    fun listZaakbeeindigRedenen(): List<ZaakbeeindigReden> {
        val builder = entityManager.criteriaBuilder
        val query = builder.createQuery(ZaakbeeindigReden::class.java)
        val root = query.from(ZaakbeeindigReden::class.java)
        query.orderBy(builder.asc(root.get<Any>("naam")))
        val emQuery = entityManager.createQuery(query)
        return emQuery.resultList
    }

    /**
     * Zaaktype is aangepast, indien geen concept, dan de zaakafhandelparameters van de vorige versie zoveel mogelijk overnemen
     *
     * @param zaaktypeUri uri van het nieuwe zaaktype
     */
    fun zaaktypeAangepast(zaaktypeUri: URI) {
        zaakafhandelParameterService.clearListCache()
        ztcClientService.clearZaaktypeCache()
        val zaaktype = ztcClientService.readZaaktype(zaaktypeUri)
        if (!zaaktype.concept) {
            val omschrijving = zaaktype.omschrijving
            val vorigeZaakafhandelparameters = readRecentsteZaakafhandelParameters(omschrijving)
            val nieuweZaakafhandelParameters = ZaakafhandelParameters().apply {
                zaakTypeUUID = UriUtil.uuidFromURI(zaaktype.url)
                zaaktypeOmschrijving = zaaktype.omschrijving
                caseDefinitionID = vorigeZaakafhandelparameters.caseDefinitionID
                groepID = vorigeZaakafhandelparameters.groepID
                gebruikersnaamMedewerker = vorigeZaakafhandelparameters.gebruikersnaamMedewerker
                einddatumGeplandWaarschuwing = zaaktype.servicenorm?.let {
                    vorigeZaakafhandelparameters.einddatumGeplandWaarschuwing
                }
                uiterlijkeEinddatumAfdoeningWaarschuwing = vorigeZaakafhandelparameters.uiterlijkeEinddatumAfdoeningWaarschuwing
                intakeMail = vorigeZaakafhandelparameters.intakeMail
                afrondenMail = vorigeZaakafhandelparameters.afrondenMail
                productaanvraagtype = vorigeZaakafhandelparameters.productaanvraagtype
                domein = vorigeZaakafhandelparameters.domein
            }
            mapHumanTaskParameters(vorigeZaakafhandelparameters, nieuweZaakafhandelParameters)
            mapUserEventListenerParameters(vorigeZaakafhandelparameters, nieuweZaakafhandelParameters)
            mapZaakbeeindigGegevens(vorigeZaakafhandelparameters, nieuweZaakafhandelParameters, zaaktype)
            mapMailtemplateKoppelingen(vorigeZaakafhandelparameters, nieuweZaakafhandelParameters)
            createZaakafhandelParameters(nieuweZaakafhandelParameters)
        }
    }

    private fun readRecentsteZaakafhandelParameters(zaaktypeOmschrijving: String): ZaakafhandelParameters {
        val builder = entityManager.criteriaBuilder
        val query = builder.createQuery(ZaakafhandelParameters::class.java)
        val root = query.from(ZaakafhandelParameters::class.java)
        query.select(root)
            .where(builder.equal(root.get<Any>(ZaakafhandelParameters.ZAAKTYPE_OMSCHRIJVING), zaaktypeOmschrijving))
        query.orderBy(builder.desc(root.get<Any>(ZaakafhandelParameters.CREATIEDATUM)))
        val resultList = entityManager.createQuery(query).setMaxResults(1).resultList
        return if (resultList.isNotEmpty()) {
            resultList.first()
        } else {
            ZaakafhandelParameters()
        }
    }

    /**
     * Kopieren van de HumanTaskParameters van de oude ZaakafhandelParameters naar de nieuw ZaakafhandelParameters
     *
     * @param vorigeZaakafhandelparameters bron
     * @param nieuweZaakafhandelParameters bestemming
     */
    private fun mapHumanTaskParameters(
        vorigeZaakafhandelparameters: ZaakafhandelParameters,
        nieuweZaakafhandelParameters: ZaakafhandelParameters
    ) = vorigeZaakafhandelparameters.humanTaskParametersCollection.map {
        HumanTaskParameters().apply {
            doorlooptijd = it.doorlooptijd
            isActief = it.isActief
            formulierDefinitieID = it.formulierDefinitieID
            planItemDefinitionID = it.planItemDefinitionID
            groepID = it.groepID
            referentieTabellen = it.referentieTabellen
            formulierDefinitieID = it.formulierDefinitieID
        }
    }.toSet().let(nieuweZaakafhandelParameters::setHumanTaskParametersCollection)

    /**
     * Kopieren van de UserEventListenerParameters van de oude ZaakafhandelParameters naar de nieuw ZaakafhandelParameters
     *
     * @param vorigeZaakafhandelparameters bron
     * @param nieuweZaakafhandelParameters bestemming
     */
    private fun mapUserEventListenerParameters(
        vorigeZaakafhandelparameters: ZaakafhandelParameters,
        nieuweZaakafhandelParameters: ZaakafhandelParameters
    ) = vorigeZaakafhandelparameters.userEventListenerParametersCollection.map {
        UserEventListenerParameters().apply {
            planItemDefinitionID = it.planItemDefinitionID
            toelichting = it.toelichting
        }
    }.toSet().let(nieuweZaakafhandelParameters::setUserEventListenerParametersCollection)

    /**
     * Kopieren van de ZaakbeeindigGegevens van de oude ZaakafhandelParameters naar de nieuw ZaakafhandelParameters
     *
     * @param vorigeZaakafhandelparameters bron
     * @param nieuweZaakafhandelParameters bestemming
     * @param nieuwZaaktype                het nieuwe zaaktype om de resultaten van te lezen
     */
    private fun mapZaakbeeindigGegevens(
        vorigeZaakafhandelparameters: ZaakafhandelParameters,
        nieuweZaakafhandelParameters: ZaakafhandelParameters,
        nieuwZaaktype: ZaakType
    ) {
        val nieuweResultaattypen = nieuwZaaktype.resultaattypen.map { ztcClientService.readResultaattype(it) }
        nieuweZaakafhandelParameters.nietOntvankelijkResultaattype =
            vorigeZaakafhandelparameters.nietOntvankelijkResultaattype?.let {
                mapVorigResultaattypeOpNieuwResultaattype(nieuweResultaattypen, it)
            }
        val zaakbeeindigParametersCollection = vorigeZaakafhandelparameters.zaakbeeindigParameters.mapNotNull {
                zaakbeeindigParameter ->
            zaakbeeindigParameter.resultaattype
                ?.let { mapVorigResultaattypeOpNieuwResultaattype(nieuweResultaattypen, it) }
                ?.let {
                    ZaakbeeindigParameter().apply {
                        zaakbeeindigReden = zaakbeeindigParameter.zaakbeeindigReden
                        resultaattype = it
                    }
                }
        }.toMutableSet()
        nieuweZaakafhandelParameters.setZaakbeeindigParameters(zaakbeeindigParametersCollection)
    }

    private fun mapMailtemplateKoppelingen(
        vorigeZaakafhandelparameters: ZaakafhandelParameters,
        nieuweZaakafhandelParameters: ZaakafhandelParameters
    ) = vorigeZaakafhandelparameters.mailtemplateKoppelingen.map {
        MailtemplateKoppeling().apply {
            mailTemplate = it.mailTemplate
            zaakafhandelParameters = nieuweZaakafhandelParameters
        }
    }.toSet().let(nieuweZaakafhandelParameters::setMailtemplateKoppelingen)

    private fun mapVorigResultaattypeOpNieuwResultaattype(
        nieuweResultaattypen: List<ResultaatType>,
        vorigResultaattypeUUID: UUID
    ): UUID? =
        ztcClientService.readResultaattype(vorigResultaattypeUUID).let { resultaattype ->
            nieuweResultaattypen
                .firstOrNull { it.omschrijving == resultaattype.omschrijving }
                ?.let { UriUtil.uuidFromURI(it.url) }
        }
}
