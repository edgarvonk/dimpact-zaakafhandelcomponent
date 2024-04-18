/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */
package net.atos.zac.app.taken.converter

import jakarta.inject.Inject
import jakarta.json.bind.annotation.JsonbDateFormat
import net.atos.zac.app.taken.model.RESTTaakHistorieRegel
import net.atos.zac.flowable.TakenService
import net.atos.zac.flowable.TakenService.ValueChangeData
import net.atos.zac.identity.IdentityService
import net.atos.zac.util.DateTimeConverterUtil
import net.atos.zac.util.JsonbUtil
import org.flowable.task.api.history.HistoricTaskLogEntry
import org.flowable.task.api.history.HistoricTaskLogEntryType
import java.util.Date

class RESTTaakHistorieConverter @Inject constructor(
    private val identityService: IdentityService
) {
    companion object {
        const val CREATED_ATTRIBUUT_LABEL = "aangemaakt"
        const val COMPLETED_ATTRIBUUT_LABEL = "afgerond"
        const val GROEP_ATTRIBUUT_LABEL = "groep"
        const val BEHANDELAAR_ATTRIBUUT_LABEL = "behandelaar"
        const val TOELICHTING_ATTRIBUUT_LABEL = "toelichting"
        const val AANGEMAAKT_DOOR_ATTRIBUUT_LABEL = "aangemaaktDoor"
        const val FATALEDATUM_ATTRIBUUT_LABEL = "fataledatum"
        const val STATUS_ATTRIBUUT_LABEL = "taak.status"
    }

    fun convert(historicTaskLogEntries: List<HistoricTaskLogEntry>): List<RESTTaakHistorieRegel> =
        historicTaskLogEntries
            .map { historicTaskLogEntry -> this.convert(historicTaskLogEntry) }
            .mapNotNull { it }
            .toList()

    private fun convert(historicTaskLogEntry: HistoricTaskLogEntry): RESTTaakHistorieRegel? {
        val restTaakHistorieRegel = when (historicTaskLogEntry.type) {
            TakenService.USER_TASK_DESCRIPTION_CHANGED -> convertValueChangeData(
                TOELICHTING_ATTRIBUUT_LABEL,
                historicTaskLogEntry.data
            )

            TakenService.USER_TASK_ASSIGNEE_CHANGED_CUSTOM -> convertValueChangeData(
                BEHANDELAAR_ATTRIBUUT_LABEL,
                historicTaskLogEntry.data
            )
            TakenService.USER_TASK_GROUP_CHANGED -> convertValueChangeData(
                GROEP_ATTRIBUUT_LABEL,
                historicTaskLogEntry.data
            )
            else -> convertData(
                HistoricTaskLogEntryType.valueOf(historicTaskLogEntry.type),
                historicTaskLogEntry.data
            )
        }
        restTaakHistorieRegel?.datumTijd = DateTimeConverterUtil.convertToZonedDateTime(historicTaskLogEntry.timeStamp)
        return restTaakHistorieRegel
    }

    private fun convertData(historicTaskLogEntryType: HistoricTaskLogEntryType, data: String): RESTTaakHistorieRegel? =
        when (historicTaskLogEntryType) {
            HistoricTaskLogEntryType.USER_TASK_CREATED -> RESTTaakHistorieRegel(
                STATUS_ATTRIBUUT_LABEL,
                null,
                CREATED_ATTRIBUUT_LABEL,
                null
            )
            HistoricTaskLogEntryType.USER_TASK_COMPLETED -> RESTTaakHistorieRegel(
                STATUS_ATTRIBUUT_LABEL,
                CREATED_ATTRIBUUT_LABEL,
                COMPLETED_ATTRIBUUT_LABEL,
                null
            )
            HistoricTaskLogEntryType.USER_TASK_OWNER_CHANGED -> convertOwnerChanged(data)
            HistoricTaskLogEntryType.USER_TASK_DUEDATE_CHANGED -> convertDuedateChanged(data)
            // unsupported types result in null return value
            else -> null
        }

    private fun convertValueChangeData(attribuutLabel: String, data: String): RESTTaakHistorieRegel {
        val valueChangeData = JsonbUtil.JSONB.fromJson(data, ValueChangeData::class.java)
        return RESTTaakHistorieRegel(
            attribuutLabel,
            valueChangeData.oldValue,
            valueChangeData.newValue,
            valueChangeData.explanation
        )
    }

    class AssigneeChangedData {
        var newAssigneeId: String? = null

        var previousAssigneeId: String? = null
    }

    private fun convertOwnerChanged(data: String): RESTTaakHistorieRegel {
        val assigneeChangedData = JsonbUtil.JSONB.fromJson(data, AssigneeChangedData::class.java)
        return RESTTaakHistorieRegel(
            AANGEMAAKT_DOOR_ATTRIBUUT_LABEL,
            getMedewerkerFullName(assigneeChangedData.previousAssigneeId),
            getMedewerkerFullName(assigneeChangedData.newAssigneeId),
            null
        )
    }

    private fun getMedewerkerFullName(medewerkerId: String?): String? {
        return if (medewerkerId == null) null else identityService.readUser(medewerkerId).fullName
    }

    class DuedateChangedData {
        @JsonbDateFormat(JsonbDateFormat.TIME_IN_MILLIS)
        var newDueDate: Date? = null

        @JsonbDateFormat(JsonbDateFormat.TIME_IN_MILLIS)
        var previousDueDate: Date? = null
    }

    private fun convertDuedateChanged(data: String): RESTTaakHistorieRegel {
        val duedateChangedData = JsonbUtil.JSONB.fromJson(data, DuedateChangedData::class.java)
        return RESTTaakHistorieRegel(
            FATALEDATUM_ATTRIBUUT_LABEL,
            DateTimeConverterUtil.convertToLocalDate(duedateChangedData.previousDueDate),
            DateTimeConverterUtil.convertToLocalDate(duedateChangedData.newDueDate),
            null
        )
    }
}