/*
 * SPDX-FileCopyrightText: 2024 Lifely
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.admin

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.Runs
import io.mockk.checkUnnecessaryStub
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import jakarta.persistence.EntityManager
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Root
import net.atos.zac.admin.model.HumanTaskReferentieTabel
import net.atos.zac.admin.model.ReferenceTable
import net.atos.zac.admin.model.createReferenceTable

class ReferenceTableAdminServiceTest : BehaviorSpec({
    val criteriaBuilder = mockk<CriteriaBuilder>()
    val criteriaQueryHumanTaskReferentieTabel = mockk<CriteriaQuery<HumanTaskReferentieTabel>>()
    val rootHumanTaskReferentieTabel = mockk<Root<HumanTaskReferentieTabel>>()
    val entityManager = mockk<EntityManager>()
    val referenceTableService = mockk<ReferenceTableService>()
    val referenceTableAdminService = ReferenceTableAdminService(
        entityManager = entityManager,
        referenceTableService = referenceTableService
    )

    beforeEach {
        checkUnnecessaryStub()
    }

    Given(
        """
            A reference table that is not a system reference table and which is not in use by any human task reference tables
            """
    ) {
        val referenceTable = createReferenceTable(
            isSystemReferenceTable = false
        )
        every { entityManager.find(ReferenceTable::class.java, referenceTable.id) } returns referenceTable
        every { entityManager.criteriaBuilder } returns criteriaBuilder
        every {
            criteriaBuilder.createQuery(HumanTaskReferentieTabel::class.java)
        } returns criteriaQueryHumanTaskReferentieTabel
        every {
            criteriaQueryHumanTaskReferentieTabel.from(HumanTaskReferentieTabel::class.java)
        } returns rootHumanTaskReferentieTabel
        every {
            criteriaQueryHumanTaskReferentieTabel.select(rootHumanTaskReferentieTabel)
        } returns criteriaQueryHumanTaskReferentieTabel
        every {
            criteriaQueryHumanTaskReferentieTabel.where(
                criteriaBuilder.equal(rootHumanTaskReferentieTabel.get<Any>("tabel").get<Any>("id"), referenceTable.id)
            )
        } returns criteriaQueryHumanTaskReferentieTabel
        every { entityManager.createQuery(criteriaQueryHumanTaskReferentieTabel).resultList } returns emptyList()
        every { entityManager.remove(referenceTable) } just Runs

        When("the reference table is deleted") {
            referenceTableAdminService.deleteReferenceTable(referenceTable.id!!)

            Then("the reference table should be successfully deleted") {
                verify(exactly = 1) {
                    entityManager.remove(referenceTable)
                }
            }
        }
    }

    Given("A system reference table") {
        val referenceTable = createReferenceTable(
            isSystemReferenceTable = true
        )
        every { entityManager.find(ReferenceTable::class.java, referenceTable.id) } returns referenceTable

        When("the reference table is deleted") {
            val exception = shouldThrow<IllegalArgumentException> {
                referenceTableAdminService.deleteReferenceTable(referenceTable.id!!)
            }

            Then("an exception should be thrown and the reference table is not deleted") {
                exception.message shouldBe "Deze referentietabel is een systeemtabel en kan niet verwijderd worden."
            }
        }
    }
})
