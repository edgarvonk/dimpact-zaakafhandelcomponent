/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AbstractFormulier} from './abstract-formulier';
import {Validators} from '@angular/forms';
import {TextareaFormFieldBuilder} from '../../shared/material-form-builder/form-components/textarea/textarea-form-field-builder';
import {TranslateService} from '@ngx-translate/core';
import {InformatieObjectenService} from '../../informatie-objecten/informatie-objecten.service';
import {TakenService} from '../../taken/taken.service';
import {ParagraphFormFieldBuilder} from '../../shared/material-form-builder/form-components/paragraph/paragraph-form-field-builder';
import {ReadonlyFormFieldBuilder} from '../../shared/material-form-builder/form-components/readonly/readonly-form-field-builder';
import {InputFormFieldBuilder} from '../../shared/material-form-builder/form-components/input/input-form-field-builder';
import {CustomValidators} from '../../shared/validators/customValidators';
import {DocumentenLijstFieldBuilder} from '../../shared/material-form-builder/form-components/documenten-lijst/documenten-lijst-field-builder';
import {InformatieobjectZoekParameters} from '../../informatie-objecten/model/informatieobject-zoek-parameters';

export class ExternAdviesMail extends AbstractFormulier {

    public static formulierDefinitie = 'EXTERN_ADVIES_MAIL';

    fields = {
        BODY: 'body',
        ADVISEUR: 'adviseur',
        EMAILADRES: 'emailadres',
        BRON: 'bron',
        EXTERNADVIES: 'externAdvies',
        BIJLAGEN: 'bijlagen'
    };

    taakinformatieMapping = {
        uitkomst: this.fields.EXTERNADVIES
    };

    constructor(
        translate: TranslateService,
        public takenService: TakenService,
        public informatieObjectenService: InformatieObjectenService) {
        super(translate, informatieObjectenService);
    }

    _initStartForm() {
        const fields = this.fields;
        this.humanTaskData.taakStuurGegevens.sendMail = true;
        this.humanTaskData.taakStuurGegevens.onderwerp = 'Advies nodig voor zaak';

        const zoekparameters = new InformatieobjectZoekParameters();
        zoekparameters.zaakUUID = this.zaakUuid;
        const documenten = this.informatieObjectenService.listEnkelvoudigInformatieobjecten(zoekparameters);

        this.form.push(
            [new InputFormFieldBuilder().id(fields.ADVISEUR).label(fields.ADVISEUR).validators(Validators.required)
                                        .maxlength(1000).build()],
            [new InputFormFieldBuilder().id(fields.EMAILADRES).label(fields.EMAILADRES)
                                        .validators(Validators.required, CustomValidators.emails).build()],
            [new TextareaFormFieldBuilder().id(fields.BODY).label(fields.BODY)
                                           .validators(Validators.required)
                                           .maxlength(1000).build()],
            [new DocumentenLijstFieldBuilder().id(fields.BIJLAGEN).label(fields.BIJLAGEN)
                                              .documenten(documenten).build()]
        );
    }

    _initBehandelForm() {
        const fields = this.fields;
        this.form.push(
            [new ParagraphFormFieldBuilder().text(this.translate.instant('msg.extern.advies.vastleggen.behandelen')).build()],
            [new ReadonlyFormFieldBuilder(this.getDataElement(fields.ADVISEUR)).id(fields.ADVISEUR)
                                                                               .label(fields.ADVISEUR)
                                                                               .build()],
            [new ReadonlyFormFieldBuilder(this.getDataElement(fields.EMAILADRES)).id(fields.EMAILADRES)
                                                                                 .label(fields.EMAILADRES)
                                                                                 .build()],
            [new ReadonlyFormFieldBuilder(this.getDataElement(fields.BODY)).id(fields.BODY)
                                                                           .label(fields.BODY)
                                                                           .build()],

            [new TextareaFormFieldBuilder(this.getDataElement(fields.EXTERNADVIES)).id(fields.EXTERNADVIES)
                                                                                   .label(fields.EXTERNADVIES)
                                                                                   .validators(Validators.required)
                                                                                   .readonly(this.readonly)
                                                                                   .maxlength(1000).build()]
        );
    }

    getBehandelTitel(): string {
        return this.translate.instant('title.taak.extern-advies.verwerken');
    }
}
