/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {PlanItemType} from './plan-item-type.enum';
import {UserEventListenerActie} from './user-event-listener-actie-enum';

export class PlanItem {
    id: string;
    naam: string;
    type: PlanItemType;
    groepId: string;
    formulierDefinitie: string;
    tabellen: { [key: string]: string[] };
    zaakUuid: string;
    userEventListenerActie: UserEventListenerActie;
    toelichting: string;
}
