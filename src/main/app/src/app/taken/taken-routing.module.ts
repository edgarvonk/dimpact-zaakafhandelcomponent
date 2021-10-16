/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {TaakViewComponent} from './taak-view/taak-view.component';
import {TaakResolver} from './taak.resolver';
import {TakenMijnComponent} from './taken-mijn/taken-mijn.component';
import {TakenWerkvoorraadComponent} from './taken-werkvoorraad/taken-werkvoorraad.component';
import {TaakBewerkenComponent} from './taak-bewerken/taak-bewerken.component';
import {TaakToekennenComponent} from './taak-toekennen/taak-toekennen.component';

const routes: Routes = [
    {
        path: 'taken', children: [
            {path: '', redirectTo: 'werkvoorraad', pathMatch: 'full'},
            {path: 'werkvoorraad', component: TakenWerkvoorraadComponent},
            {path: 'mijn', component: TakenMijnComponent},
            {path: ':id', component: TaakViewComponent, resolve: {taak: TaakResolver}},
            {path: ':id/edit', component: TaakBewerkenComponent, resolve: {taak: TaakResolver}},
            {path: ':id/toekennen', component: TaakToekennenComponent, resolve: {taak: TaakResolver}}
        ]
    }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class TakenRoutingModule {
}
