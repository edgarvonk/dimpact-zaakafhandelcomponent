/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos, 2024 Lifely
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { Component, EventEmitter, Input, OnInit, Output } from "@angular/core";
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { MatSidenav } from "@angular/material/sidenav";
import { MatTableDataSource } from "@angular/material/table";
import { Router } from "@angular/router";
import { Subject, Subscription, forkJoin } from "rxjs";
import { ConfiguratieService } from "../../../configuratie/configuratie.service";
import { UtilService } from "../../../core/service/util.service";
import { ActionIcon } from "../../../shared/edit/action-icon";
import { DateFormFieldBuilder } from "../../../shared/material-form-builder/form-components/date/date-form-field-builder";
import { InputFormFieldBuilder } from "../../../shared/material-form-builder/form-components/input/input-form-field-builder";
import { AbstractFormControlField } from "../../../shared/material-form-builder/model/abstract-form-control-field";
import { GeneratedType } from "../../../shared/utils/generated-types";
import { CustomValidators } from "../../../shared/validators/customValidators";
import { KlantenService } from "../../klanten.service";
import { Cardinaliteit } from "../../model/personen/cardinaliteit";
import { ListPersonenParameters } from "../../model/personen/list-personen-parameters";
import { PersonenParameters } from "../../model/personen/personen-parameters";
import { FormCommunicatieService } from "../form-communicatie-service";

@Component({
  selector: "zac-persoon-zoek",
  templateUrl: "./persoon-zoek.component.html",
  styleUrls: ["./persoon-zoek.component.less"],
})
export class PersoonZoekComponent implements OnInit {
  @Output() persoon? = new EventEmitter<GeneratedType<"RestPersoon">>();
  @Input() sideNav?: MatSidenav;
  @Input() syncEnabled: boolean = false;
  formGroup: FormGroup;
  bsnFormField: AbstractFormControlField;
  geslachtsnaamFormField: AbstractFormControlField;
  voornamenFormField: AbstractFormControlField;
  voorvoegselFormField: AbstractFormControlField;
  geboortedatumFormField: AbstractFormControlField;
  gemeenteVanInschrijvingFormField: AbstractFormControlField;
  straatFormField: AbstractFormControlField;
  postcodeFormField: AbstractFormControlField;
  huisnummerFormField: AbstractFormControlField;
  queryFields;
  queries: PersonenParameters[] = [];
  persoonColumns: string[] = [
    "bsn",
    "naam",
    "geboortedatum",
    "verblijfplaats",
    "acties",
  ];
  personen = new MatTableDataSource<GeneratedType<"RestPersoon">>();
  mijnGemeente: string;
  foutmelding: string;
  loading = false;
  uuid: string;
  private formSelectedSubscription!: Subscription;

  constructor(
    private klantenService: KlantenService,
    private utilService: UtilService,
    private formBuilder: FormBuilder,
    private router: Router,
    private configuratieService: ConfiguratieService,
    private formCommunicationService: FormCommunicatieService,
  ) {}

  ngOnInit(): void {
    this.bsnFormField = new InputFormFieldBuilder()
      .id("bsn")
      .label("bsn")
      .validators(CustomValidators.bsn)
      .maxlength(9)
      .build();
    this.voornamenFormField = new InputFormFieldBuilder()
      .id("voornamen")
      .label("voornamen")
      .maxlength(50)
      .build();
    this.geslachtsnaamFormField = new InputFormFieldBuilder()
      .id("achternaam")
      .label("achternaam")
      .maxlength(50)
      .build();
    this.voorvoegselFormField = new InputFormFieldBuilder()
      .id("voorvoegsel")
      .label("voorvoegsel")
      .maxlength(10)
      .build();
    this.geboortedatumFormField = new DateFormFieldBuilder()
      .id("geboortedatum")
      .label("geboortedatum")
      .build();
    const gemeenteIcon: ActionIcon = new ActionIcon(
      "location_city",
      "gemeenteMijn",
      new Subject<void>(),
    );
    this.gemeenteVanInschrijvingFormField = new InputFormFieldBuilder()
      .id("gemeenteVanInschrijving")
      .label("gemeenteVanInschrijving")
      .validators(Validators.min(1), Validators.max(9999))
      .maxlength(4)
      .icon(gemeenteIcon)
      .build();
    gemeenteIcon.iconClicked.subscribe(() => {
      this.gemeenteVanInschrijvingFormField.formControl.setValue(
        this.mijnGemeente,
      );
    });
    this.straatFormField = new InputFormFieldBuilder()
      .id("straat")
      .label("straat")
      .maxlength(55)
      .build();
    this.postcodeFormField = new InputFormFieldBuilder()
      .id("postcode")
      .label("postcode")
      .validators(CustomValidators.postcode)
      .maxlength(7)
      .build();
    this.huisnummerFormField = new InputFormFieldBuilder()
      .id("huisnummer")
      .label("huisnummer")
      .validators(
        Validators.min(1),
        Validators.max(99999),
        CustomValidators.huisnummer,
      )
      .maxlength(5)
      .build();

    this.queryFields = {
      bsn: this.bsnFormField,
      geslachtsnaam: this.geslachtsnaamFormField,
      voornamen: this.voornamenFormField,
      voorvoegsel: this.voorvoegselFormField,
      geboortedatum: this.geboortedatumFormField,
      gemeenteVanInschrijving: this.gemeenteVanInschrijvingFormField,
      straat: this.straatFormField,
      postcode: this.postcodeFormField,
      huisnummer: this.huisnummerFormField,
    };

    this.formGroup = this.formBuilder.group({
      bsn: this.bsnFormField.formControl,
      geslachtsnaam: this.geslachtsnaamFormField.formControl,
      voornamen: this.voornamenFormField.formControl,
      voorvoegsel: this.voorvoegselFormField.formControl,
      geboortedatum: this.geboortedatumFormField.formControl,
      gemeenteVanInschrijving:
        this.gemeenteVanInschrijvingFormField.formControl,
      straat: this.straatFormField.formControl,
      postcode: this.postcodeFormField.formControl,
      huisnummer: this.huisnummerFormField.formControl,
    });

    forkJoin([
      this.klantenService.getPersonenParameters(),
      this.configuratieService.readGemeenteCode(),
    ]).subscribe(([personenParameters, gemeenteCode]) => {
      this.queries = personenParameters;
      this.mijnGemeente = gemeenteCode;
    });

    this.uuid = crypto.randomUUID(); // Generate a unique form ID

    if (this.syncEnabled) {
      // Subscribe to select event, ignore own event
      this.formSelectedSubscription =
        this.formCommunicationService.itemSelected$.subscribe(
          ({ selected, uuid }) => {
            if (selected && uuid !== this.uuid) {
              this.wissen();
            }
          },
        );
    }
  }

  isValid(): boolean {
    const parameters: ListPersonenParameters =
      this.createListPersonenParameters();
    this.updateControls(this.getValidQueries(parameters, false));
    return (
      this.formGroup.valid && 0 < this.getValidQueries(parameters, true).length
    );
  }

  private getValidQueries(
    values: ListPersonenParameters,
    compleet: boolean,
  ): PersonenParameters[] {
    let validQueries: PersonenParameters[] = this.queries;
    for (const key in this.queryFields) {
      if (values[key] != null) {
        // Verwijder alle queries die met dit gevulde veld niet kunnen
        validQueries = this.exclude(validQueries, key, Cardinaliteit.NON);
      } else {
        if (compleet) {
          // Verwijder alles queries die zonder dit lege veld niet kunnen
          validQueries = this.exclude(validQueries, key, Cardinaliteit.REQ);
        }
      }
    }
    return validQueries;
  }

  exclude(
    queries: PersonenParameters[],
    key: string,
    value: Cardinaliteit,
  ): PersonenParameters[] {
    return queries.filter((query) => query[key] !== value);
  }

  include(
    queries: PersonenParameters[],
    key: string,
    value: Cardinaliteit,
  ): PersonenParameters[] {
    return queries.filter((query) => query[key] === value);
  }

  all(queries: PersonenParameters[], key: string, value: Cardinaliteit) {
    return this.include(queries, key, value).length === queries.length;
  }

  private updateControls(potential: PersonenParameters[]) {
    for (const key in this.queryFields) {
      if (this.queryFields.hasOwnProperty(key)) {
        const control: AbstractFormControlField = this.queryFields[key];
        if (this.all(potential, key, Cardinaliteit.NON)) {
          this.requireField(control, false);
          this.enableField(control, false);
        } else {
          this.requireField(
            control,
            this.all(potential, key, Cardinaliteit.REQ),
          );
          this.enableField(control, true);
        }
      }
    }
  }

  private requireField(control: AbstractFormControlField, required: boolean) {
    control.required = required;
    if (required) {
      control.formControl.addValidators(Validators.required);
    } else {
      control.formControl.removeValidators(Validators.required);
    }
  }

  private enableField(control: AbstractFormControlField, enabled: boolean) {
    if (enabled) {
      control.formControl.enable();
    } else {
      control.formControl.setValue(null);
      control.formControl.disable();
    }
  }

  createListPersonenParameters(): ListPersonenParameters {
    const params = new ListPersonenParameters();
    for (const entry of Object.entries(this.formGroup.value)) {
      const k = entry[0];
      let v = entry[1];
      if (typeof v === "string") {
        v = v.trim();
      }
      if (v) {
        params[k] = v;
      }
    }
    return params;
  }

  zoekPersonen(): void {
    this.loading = true;
    this.utilService.setLoading(true);
    this.personen.data = [];
    this.klantenService
      .listPersonen(this.createListPersonenParameters())
      .subscribe((personen) => {
        this.personen.data = personen.resultaten;
        this.foutmelding = personen.foutmelding;
        this.loading = false;
        this.utilService.setLoading(false);
      });
  }

  selectPersoon(persoon: GeneratedType<"RestPersoon">): void {
    this.persoon.emit(persoon);
    this.wissen();

    if (this.syncEnabled) {
      this.formCommunicationService.notifyItemSelected(this.uuid);
    }
  }

  openPersoonPagina(persoon: GeneratedType<"RestPersoon">): void {
    this.sideNav?.close();
    this.router.navigate(["/persoon/", persoon.identificatie]);
  }

  wissen() {
    this.formGroup.reset();
    this.personen.data = [];
  }

  ngOnDestroy() {
    if (this.formSelectedSubscription) {
      this.formSelectedSubscription.unsubscribe();
    }
  }
}
