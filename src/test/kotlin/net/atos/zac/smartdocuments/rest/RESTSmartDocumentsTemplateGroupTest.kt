package net.atos.zac.smartdocuments.rest

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe
import java.util.UUID

class RESTSmartDocumentsTemplateGroupTest : BehaviorSpec({

    Given("a REST request") {
        val expectedInformatieobjectTypeUUID = UUID.randomUUID()
        val restTemplateRequest = setOf(
            createRESTMappedTemplateGroup(name = "root").apply {
                groups = setOf(
                    createRESTMappedTemplateGroup(name = "group 1").apply {
                        templates = setOf(
                            createRESTMappedTemplate(
                                name = "group 1 template 1",
                                informatieObjectTypeUUID = expectedInformatieobjectTypeUUID
                            ),
                            createRESTMappedTemplate(
                                name = "group 1 template 2",
                                informatieObjectTypeUUID = expectedInformatieobjectTypeUUID
                            )
                        )
                        groups = emptySet()
                    },
                    createRESTMappedTemplateGroup(name = "group 2").apply {
                        templates = setOf(
                            createRESTMappedTemplate(
                                name = "group 2 template 1",
                                informatieObjectTypeUUID = expectedInformatieobjectTypeUUID
                            ),
                            createRESTMappedTemplate(
                                name = "group 2 template 2",
                                informatieObjectTypeUUID = expectedInformatieobjectTypeUUID
                            )
                        )
                        groups = emptySet()
                    }
                )
                templates = setOf(
                    createRESTMappedTemplate(
                        name = "root template 1",
                        informatieObjectTypeUUID = expectedInformatieobjectTypeUUID
                    ),
                    createRESTMappedTemplate(
                        name = "root template 2",
                        informatieObjectTypeUUID = expectedInformatieobjectTypeUUID
                    )
                )
            }
        )

        When("convert to string representation is requested") {
            val stringSet = restTemplateRequest.toStringRepresentation()

            Then("it produces a correct set of strings") {
                stringSet.size shouldBe 9

                with(restTemplateRequest.first()) {
                    val rootId = id
                    val rootTemplate1Id = templates!!.first().id
                    val rootTemplate2Id = templates!!.last().id
                    val group1Id = groups!!.first().id
                    val group2Id = groups!!.last().id
                    val group1Template1Id = groups!!.first().templates!!.first().id
                    val group1Template2Id = groups!!.first().templates!!.last().id
                    val group2Template1Id = groups!!.last().templates!!.first().id
                    val group2Template2Id = groups!!.last().templates!!.last().id

                    stringSet shouldContainAll setOf(
                        "group.$rootId.root",
                        "group.$rootId.root.template.$rootTemplate1Id.root template 1",
                        "group.$rootId.root.template.$rootTemplate2Id.root template 2",
                        "group.$rootId.root.group.$group1Id.group 1",
                        "group.$rootId.root.group.$group1Id.group 1.template.$group1Template1Id.group 1 template 1",
                        "group.$rootId.root.group.$group1Id.group 1.template.$group1Template2Id.group 1 template 2",
                        "group.$rootId.root.group.$group2Id.group 2",
                        "group.$rootId.root.group.$group2Id.group 2.template.$group2Template1Id.group 2 template 1",
                        "group.$rootId.root.group.$group2Id.group 2.template.$group2Template2Id.group 2 template 2"
                    )
                }
            }
        }
    }
})
