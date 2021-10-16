/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {Component, OnInit} from '@angular/core';
import {FormItem} from '../../shared/material-form-builder/model/form-item';
import {Zaak} from '../model/zaak';
import {FormGroup, Validators} from '@angular/forms';
import {FormConfig} from '../../shared/material-form-builder/model/form-config';
import {ActivatedRoute} from '@angular/router';
import {MaterialFormBuilderService} from '../../shared/material-form-builder/material-form-builder.service';
import {IdentityService} from '../../identity/identity.service';
import {NavigationService} from '../../shared/navigation/navigation.service';
import {ZakenService} from '../zaken.service';
import {UtilService} from '../../core/service/util.service';
import {Title} from '@angular/platform-browser';
import {FormFieldConfig} from '../../shared/material-form-builder/model/form-field-config';

@Component({
    templateUrl: './zaak-toekennen.component.html',
    styleUrls: ['./zaak-toekennen.component.less']
})
export class ZaakToekennenComponent implements OnInit {

    formItems: Array<FormItem[]>;
    formConfig: FormConfig;
    zaak: Zaak;

    constructor(private route: ActivatedRoute, private mfbService: MaterialFormBuilderService, private identityService: IdentityService,
                private navigation: NavigationService, private zakenService: ZakenService, private titleService: Title, private utilService: UtilService) {
    }

    ngOnInit(): void {
        this.zaak = this.route.snapshot.data['zaak'];

        this.titleService.setTitle(`${this.zaak.identificatie} | Zaak toekennen`);
        this.utilService.setHeaderTitle(`${this.zaak.identificatie} | Zaak toekennen`);

        this.identityService.getMedewerkersInGroep(this.zaak.groep.id).subscribe(medewerkers => {
            this.identityService.getIngelogdeMedewerker().subscribe(ingelogdeMedewerker => {
                const medewerker = this.mfbService.createSelectFormItem('medewerker', 'Medewerker',
                    this.zaak.behandelaar ? this.zaak.behandelaar : ingelogdeMedewerker, 'naam', medewerkers,
                    new FormFieldConfig([Validators.required]));
                this.formItems = [[medewerker]];
            });
        });
        this.formConfig = new FormConfig('Toekennen', 'Annuleren');
    }

    onFormSubmit(formGroup: FormGroup): void {
        if (formGroup) {
            this.zaak.behandelaar = formGroup.controls['medewerker']?.value;
            this.zakenService.toekennen(this.zaak).subscribe(() => {
                this.navigation.back();
            });
        } else {
            this.navigation.back();
        }
    }

}
