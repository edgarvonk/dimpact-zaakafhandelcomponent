/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.app.taken.converter;

import static net.atos.zac.app.taken.model.TaakStatus.AFGEROND;
import static net.atos.zac.app.taken.model.TaakStatus.NIET_TOEGEKEND;
import static net.atos.zac.app.taken.model.TaakStatus.TOEGEKEND;
import static net.atos.zac.util.DateTimeConverterUtil.convertToDate;
import static net.atos.zac.util.DateTimeConverterUtil.convertToLocalDate;
import static net.atos.zac.util.DateTimeConverterUtil.convertToZonedDateTime;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.flowable.identitylink.api.IdentityLinkInfo;
import org.flowable.identitylink.api.IdentityLinkType;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskInfo;

import net.atos.zac.app.identity.converter.RESTGroepConverter;
import net.atos.zac.app.identity.converter.RESTMedewerkerConverter;
import net.atos.zac.app.taken.model.RESTTaak;
import net.atos.zac.app.taken.model.TaakStatus;
import net.atos.zac.authentication.IngelogdeMedewerker;
import net.atos.zac.authentication.Medewerker;
import net.atos.zac.flowable.FlowableService;
import net.atos.zac.rechten.RechtOperatie;
import net.atos.zac.rechten.TaakRechten;

/**
 *
 */
public class RESTTaakConverter {

    @Inject
    private FlowableService flowableService;

    @Inject
    private RESTGroepConverter groepConverter;

    @Inject
    private RESTMedewerkerConverter medewerkerConverter;

    @Inject
    @IngelogdeMedewerker
    private Medewerker ingelogdeMedewerker;

    public List<RESTTaak> convertTaskInfoList(final List<? extends TaskInfo> tasks) {
        return tasks.stream()
                .map(this::convertTaskInfo)
                .collect(Collectors.toList());
    }

    public RESTTaak convertTaskInfo(final TaskInfo task) {
        final RESTTaak restTaak = new RESTTaak();
        restTaak.id = task.getId();
        restTaak.naam = task.getName();
        restTaak.toelichting = task.getDescription();
        restTaak.creatiedatumTijd = convertToZonedDateTime(task.getCreateTime());
        restTaak.toekenningsdatumTijd = convertToZonedDateTime(task.getClaimTime());
        restTaak.streefdatum = convertToLocalDate(task.getDueDate());
        restTaak.behandelaar = medewerkerConverter.convertGebruikersnaam(task.getAssignee());
        restTaak.groep = groepConverter.convertGroupId(extractGroupId(task.getIdentityLinks()));
        restTaak.status = convertToStatus(task);
        restTaak.zaakUUID = flowableService.readZaakUuidForTask(task.getId());
        restTaak.zaakIdentificatie = flowableService.readZaakIdentificatieForTask(task.getId());
        restTaak.zaaktypeOmschrijving = flowableService.readZaaktypeOmschrijvingorTask(task.getId());

        //TODO ESUITEDEV-25820 rechtencheck met solrTaak
        restTaak.rechten = getRechten(task);
        return restTaak;
    }

    public RESTTaak convertTaskInfo(final TaskInfo task, final Map<String, String> taakdata) {
        final RESTTaak restTaak = convertTaskInfo(task);
        restTaak.taakdata = taakdata;
        return restTaak;
    }

    public RESTTaak convertTaskInfo(final TaskInfo task, final String taakBehandelFormulier, final Map<String, String> taakdata) {
        final RESTTaak restTaak = convertTaskInfo(task, taakdata);
        restTaak.taakBehandelFormulier = taakBehandelFormulier;
        return restTaak;
    }

    public void convertRESTTaak(final RESTTaak restTaak, final Task task) {
        task.setDescription(restTaak.toelichting);
        task.setDueDate(convertToDate(restTaak.streefdatum));
    }

    private static String extractGroupId(final List<? extends IdentityLinkInfo> identityLinks) {
        return identityLinks.stream()
                .filter(identityLinkInfo -> IdentityLinkType.CANDIDATE.equals(identityLinkInfo.getType()))
                .findAny()
                .map(IdentityLinkInfo::getGroupId)
                .orElse(null);
    }

    private static TaakStatus convertToStatus(final TaskInfo taskInfo) {
        if (taskInfo instanceof Task) {
            if (taskInfo.getAssignee() == null) {
                return NIET_TOEGEKEND;
            } else {
                return TOEGEKEND;
            }
        } else {
            return AFGEROND;
        }
    }

    private Map<RechtOperatie, Boolean> getRechten(final TaskInfo taskInfo) {
        final Map<RechtOperatie, Boolean> rechten = new HashMap<>();

        final String groepId = extractGroupId(taskInfo.getIdentityLinks());
        final TaakStatus status = convertToStatus(taskInfo);

        rechten.put(RechtOperatie.TOEKENNEN, TaakRechten.isToekennenToegestaan(ingelogdeMedewerker, taskInfo.getAssignee(), groepId));
        rechten.put(RechtOperatie.VRIJGEVEN, TaakRechten.isVrijgevenToegestaan(ingelogdeMedewerker, taskInfo.getAssignee(), groepId, status));
        rechten.put(RechtOperatie.TOEKENNEN_AAN_MIJ, TaakRechten.isKenToeAanMijToegestaan(ingelogdeMedewerker, taskInfo.getAssignee(), groepId, status));
        rechten.put(RechtOperatie.BEHANDELEN, TaakRechten.isBehandelenToegestaan(ingelogdeMedewerker, taskInfo.getAssignee(), groepId, status));

        return rechten;
    }
}
