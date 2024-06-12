/*
 * SPDX-FileCopyrightText: 2024 Lifely
 * SPDX-License-Identifier: EUPL-1.2+
 */
package net.atos.zac.enkelvoudiginformatieobject

import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import jakarta.transaction.Transactional
import jakarta.transaction.Transactional.TxType.REQUIRED
import jakarta.transaction.Transactional.TxType.SUPPORTS
import net.atos.client.zgw.drc.DrcClientService
import net.atos.client.zgw.zrc.ZRCClientService
import net.atos.client.zgw.zrc.model.Zaak
import net.atos.zac.enkelvoudiginformatieobject.model.EnkelvoudigInformatieObjectLock
import net.atos.zac.util.UriUtil
import nl.lifely.zac.util.AllOpen
import nl.lifely.zac.util.NoArgConstructor
import java.util.Optional
import java.util.UUID

@ApplicationScoped
@Transactional(SUPPORTS)
@AllOpen
@NoArgConstructor
class EnkelvoudigInformatieObjectLockService @Inject constructor(
    private val drcClientService: DrcClientService,
    private val zrcClientService: ZRCClientService
) {
    @PersistenceContext(unitName = "ZaakafhandelcomponentPU")
    private lateinit var entityManager: EntityManager

    @Transactional(REQUIRED)
    fun createLock(informationObjectUUID: UUID, idUser: String): EnkelvoudigInformatieObjectLock =
        EnkelvoudigInformatieObjectLock().apply {
            enkelvoudiginformatieobjectUUID = informationObjectUUID
            userId = idUser
            lock = drcClientService.lockEnkelvoudigInformatieobject(informationObjectUUID)
            entityManager.persist(this)
        }

    fun findLock(informationObjectUUID: UUID): Optional<EnkelvoudigInformatieObjectLock> {
        val builder = entityManager.criteriaBuilder
        val query = builder.createQuery(
            EnkelvoudigInformatieObjectLock::class.java
        )
        val root = query.from(
            EnkelvoudigInformatieObjectLock::class.java
        )
        query.select(root)
            .where(builder.equal(root.get<Any>("enkelvoudiginformatieobjectUUID"), informationObjectUUID))
        val resultList = entityManager.createQuery(query).resultList
        return if (resultList.isEmpty()) Optional.empty() else Optional.of(resultList.first())
    }

    fun readLock(informationObjectUUID: UUID): EnkelvoudigInformatieObjectLock =
        findLock(informationObjectUUID).orElseThrow {
            RuntimeException("Lock for EnkelvoudigInformatieObject with uuid '$informationObjectUUID' not found")
        }

    @Transactional(REQUIRED)
    fun deleteLock(informationObjectUUID: UUID) =
        findLock(informationObjectUUID).ifPresent { lock ->
            drcClientService.unlockEnkelvoudigInformatieobject(informationObjectUUID, lock.lock)
            entityManager.remove(lock)
        }

    fun hasLockedInformatieobjecten(zaak: Zaak): Boolean {
        val informationObjectUUIDs = zrcClientService.listZaakinformatieobjecten(zaak)
            .map { UriUtil.uuidFromURI(it.informatieobject) }
            .toList()
        if (informationObjectUUIDs.isEmpty()) {
            return false
        }
        val builder = entityManager.criteriaBuilder
        val query = builder.createQuery(
            EnkelvoudigInformatieObjectLock::class.java
        )
        val root = query.from(
            EnkelvoudigInformatieObjectLock::class.java
        )
        query.select(root).where(root.get<Any>("enkelvoudiginformatieobjectUUID").`in`(informationObjectUUIDs))
        return entityManager.createQuery(query).resultList.isNotEmpty()
    }
}
