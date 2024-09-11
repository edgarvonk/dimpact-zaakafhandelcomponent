/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {
  AfterViewInit,
  Component,
  OnDestroy,
  OnInit,
  ViewChild,
} from "@angular/core";
import { FormGroup } from "@angular/forms";
import { MatDialog } from "@angular/material/dialog";
import { MatSidenav, MatSidenavContainer } from "@angular/material/sidenav";
import { MatSort } from "@angular/material/sort";
import { MatTableDataSource } from "@angular/material/table";
import { ActivatedRoute } from "@angular/router";
import { TranslateService } from "@ngx-translate/core";
import { FormulierDefinitie } from "../../admin/model/formulieren/formulier-definitie";
import { UtilService } from "../../core/service/util.service";
import { ObjectType } from "../../core/websocket/model/object-type";
import { Opcode } from "../../core/websocket/model/opcode";
import { WebsocketListener } from "../../core/websocket/model/websocket-listener";
import { WebsocketService } from "../../core/websocket/websocket.service";
import { AbstractTaakFormulier } from "../../formulieren/taken/abstract-taak-formulier";
import { TaakFormulierenService } from "../../formulieren/taken/taak-formulieren.service";
import { IdentityService } from "../../identity/identity.service";
import { User } from "../../identity/model/user";
import { InformatieObjectenService } from "../../informatie-objecten/informatie-objecten.service";
import { DocumentCreationData } from "../../informatie-objecten/model/document-creation-data";
import { EnkelvoudigInformatieobject } from "../../informatie-objecten/model/enkelvoudig-informatieobject";
import { ActionsViewComponent } from "../../shared/abstract-view/actions-view-component";
import { TextIcon } from "../../shared/edit/text-icon";
import { TaakHistorieRegel } from "../../shared/historie/model/taak-historie-regel";
import { InputFormFieldBuilder } from "../../shared/material-form-builder/form-components/input/input-form-field-builder";
import { MedewerkerGroepFieldBuilder } from "../../shared/material-form-builder/form-components/medewerker-groep/medewerker-groep-field-builder";
import { TextareaFormFieldBuilder } from "../../shared/material-form-builder/form-components/textarea/textarea-form-field-builder";
import { FormConfig } from "../../shared/material-form-builder/model/form-config";
import { FormConfigBuilder } from "../../shared/material-form-builder/model/form-config-builder";
import {
  NotificationDialogComponent,
  NotificationDialogData,
} from "../../shared/notification-dialog/notification-dialog.component";
import { ButtonMenuItem } from "../../shared/side-nav/menu-item/button-menu-item";
import { HeaderMenuItem } from "../../shared/side-nav/menu-item/header-menu-item";
import { MenuItem } from "../../shared/side-nav/menu-item/menu-item";
import { SideNavAction } from "../../shared/side-nav/side-nav-action";
import { DateConditionals } from "../../shared/utils/date-conditionals";
import { Zaak } from "../../zaken/model/zaak";
import { Zaaktype } from "../../zaken/model/zaaktype";
import { ZakenService } from "../../zaken/zaken.service";
import { Taak } from "../model/taak";
import { TaakStatus } from "../model/taak-status.enum";
import { TakenService } from "../taken.service";

@Component({
  templateUrl: "./taak-view.component.html",
  styleUrls: ["./taak-view.component.less"],
})
export class TaakViewComponent
  extends ActionsViewComponent
  implements OnInit, AfterViewInit, OnDestroy
{
  @ViewChild("actionsSidenav") actionsSidenav: MatSidenav;
  @ViewChild("menuSidenav") menuSidenav: MatSidenav;
  @ViewChild("sideNavContainer") sideNavContainer: MatSidenavContainer;
  @ViewChild("historieSort") historieSort: MatSort;

  taak: Taak;
  zaak: Zaak;
  formulier: AbstractTaakFormulier;
  formConfig: FormConfig;
  formulierDefinitie: FormulierDefinitie;
  formioFormulier: any;

  menu: MenuItem[] = [];
  readonly sideNavAction = SideNavAction;
  action: SideNavAction;

  historieSrc: MatTableDataSource<TaakHistorieRegel> =
    new MatTableDataSource<TaakHistorieRegel>();
  historieColumns: string[] = [
    "datum",
    "wijziging",
    "oudeWaarde",
    "nieuweWaarde",
    "toelichting",
  ];

  editFormFields: Map<string, any> = new Map<string, any>();
  fataledatumIcon: TextIcon;
  initialized = false;

  posts = 0;
  private taakListener: WebsocketListener;
  private ingelogdeMedewerker: User;
  readonly TaakStatusAfgerond = TaakStatus.Afgerond;

  constructor(
    private route: ActivatedRoute,
    private takenService: TakenService,
    private zakenService: ZakenService,
    private informatieObjectenService: InformatieObjectenService,
    public utilService: UtilService,
    private dialog: MatDialog,
    private websocketService: WebsocketService,
    private taakFormulierenService: TaakFormulierenService,
    private identityService: IdentityService,
    protected translate: TranslateService,
  ) {
    super();
  }

  ngOnInit(): void {
    this.getIngelogdeMedewerker();
    this.route.data.subscribe((data) => {
      this.createZaakFromTaak(data.taak);
      this.init(data.taak, true);
    });
  }

  ngAfterViewInit(): void {
    super.ngAfterViewInit();

    this.taakListener = this.websocketService.addListenerWithSnackbar(
      Opcode.ANY,
      ObjectType.TAAK,
      this.taak.id,
      () => this.reloadTaak(),
    );

    this.historieSrc.sortingDataAccessor = (item, property) => {
      switch (property) {
        case "datum":
          return item.datumTijd;
        default:
          return item[property];
      }
    };
    this.historieSrc.sort = this.historieSort;
  }

  ngOnDestroy() {
    super.ngOnDestroy();
    this.websocketService.removeListener(this.taakListener);
  }

  private initTaakGegevens(taak: Taak): void {
    this.menu = [];
    this.taak = taak;
    this.loadHistorie();
    this.setEditableFormFields();
    this.setupMenu();
  }

  private init(taak: Taak, initZaak: boolean): void {
    this.initTaakGegevens(taak);
    if (initZaak) {
      this.zakenService.readZaak(this.taak.zaakUuid).subscribe((zaak) => {
        this.zaak = zaak;
        this.initialized = true;
        this.createTaakForm(taak, zaak);
      });
    } else {
      this.createTaakForm(taak, this.zaak);
    }
  }

  private createTaakForm(taak: Taak, zaak: Zaak): void {
    if (taak.formulierDefinitieId) {
      this.createHardCodedTaakForm(taak, zaak);
    } else if (taak.formulierDefinitie) {
      this.createConfigurableTaakForm(taak);
    } else {
      this.createFormioForm(taak);
    }
  }

  private createHardCodedTaakForm(taak: Taak, zaak: Zaak): void {
    if (
      this.taak.status !== TaakStatus.Afgerond &&
      this.taak.rechten.wijzigen
    ) {
      this.formConfig = new FormConfigBuilder()
        .partialText("actie.opslaan")
        .saveText("actie.opslaan.afronden")
        .build();
    } else {
      this.formConfig = null;
    }

    this.formulier = this.taakFormulierenService
      .getFormulierBuilder(this.taak.formulierDefinitieId)
      .behandelForm(taak, zaak)
      .build();
    if (this.formulier.disablePartialSave && this.formConfig) {
      this.formConfig.partialButtonText = null;
    }
    this.utilService.setTitle("title.taak", {
      taak: this.formulier.getBehandelTitel(),
    });
  }

  private createConfigurableTaakForm(taak: Taak): void {
    this.formulierDefinitie = taak.formulierDefinitie;
    this.utilService.setTitle("title.taak", {
      taak: this.formulierDefinitie.naam,
    });
  }

  private createFormioForm(taak: Taak): void {
    this.formioFormulier = taak.formioFormulier;
    this.utilService.setTitle("title.taak", {
      taak: this.formioFormulier.title,
    });
  }

  isReadonly() {
    return (
      this.taak.status === TaakStatus.Afgerond || !this.taak.rechten.wijzigen
    );
  }

  private setEditableFormFields(): void {
    this.editFormFields.set(
      "medewerker-groep",
      new MedewerkerGroepFieldBuilder(this.taak.groep, this.taak.behandelaar)
        .id("medewerker-groep")
        .groepLabel("groep.-kies-")
        .groepRequired()
        .medewerkerLabel("behandelaar.-kies-")
        .build(),
    );
    this.editFormFields.set(
      "toelichting",
      new TextareaFormFieldBuilder(this.taak.toelichting)
        .id("toelichting")
        .label("toelichting")
        .maxlength(1000)
        .build(),
    );
    this.editFormFields.set(
      "reden",
      new InputFormFieldBuilder()
        .id("reden")
        .label("reden")
        .maxlength(80)
        .build(),
    );

    this.fataledatumIcon = new TextIcon(
      DateConditionals.provideFormControlValue(DateConditionals.isExceeded),
      "report_problem",
      "errorTaakVerlopen_icon",
      "msg.datum.overschreden",
      "error",
    );
  }

  private setupMenu(): void {
    this.menu.push(new HeaderMenuItem("taak"));

    if (this.taak.rechten.toevoegenDocument) {
      this.menu.push(
        new ButtonMenuItem(
          "actie.document.maken",
          () => {
            this.createDocumentAttended();
          },
          "note_add",
        ),
      );
      this.menu.push(
        new ButtonMenuItem(
          "actie.document.toevoegen",
          () => {
            this.actionsSidenav.open();
            this.action = SideNavAction.DOCUMENT_TOEVOEGEN;
          },
          "upload_file",
        ),
      );
    }
  }

  private createDocumentAttended(): void {
    const documentCreationData = new DocumentCreationData();
    documentCreationData.zaakUUID = this.taak.zaakUuid;
    documentCreationData.taskId = this.taak.id;
    this.informatieObjectenService
      .createDocumentAttended(documentCreationData)
      .subscribe((documentCreatieResponse) => {
        if (documentCreatieResponse.redirectURL) {
          window.open(documentCreatieResponse.redirectURL);
        } else {
          this.dialog.open(NotificationDialogComponent, {
            data: new NotificationDialogData(documentCreatieResponse.message),
          });
        }
      });
  }

  private loadHistorie(): void {
    this.takenService
      .listHistorieVoorTaak(this.taak.id)
      .subscribe((historie) => {
        this.historieSrc.data = historie;
      });
  }

  onHardCodedFormPartial(formGroup: FormGroup): void {
    this.websocketService.suspendListener(this.taakListener);
    this.takenService
      .updateTaakdata(this.formulier.getTaak(formGroup))
      .subscribe((taak) => {
        this.utilService.openSnackbar("msg.taak.opgeslagen");
        this.init(taak, false);
        this.posts++;
      });
  }

  onHardCodedFormSubmit(formGroup: FormGroup): void {
    if (formGroup) {
      this.websocketService.suspendListener(this.taakListener);
      this.takenService
        .complete(this.formulier.getTaak(formGroup))
        .subscribe((taak) => {
          this.utilService.openSnackbar("msg.taak.afgerond");
          this.init(taak, false);
        });
    }
  }

  onConfigurableFormPartial(formState: {}): void {
    if (formState) {
      this.websocketService.suspendListener(this.taakListener);
      this.taak.taakdata = formState;
      this.takenService.updateTaakdata(this.taak).subscribe((taak) => {
        this.utilService.openSnackbar("msg.taak.opgeslagen");
        this.init(taak, false);
        this.posts++;
      });
    }
  }

  onConfigurableFormSubmit(formState: {}): void {
    if (formState) {
      this.websocketService.suspendListener(this.taakListener);
      this.taak.taakdata = formState;
      this.takenService.complete(this.taak).subscribe((taak) => {
        this.utilService.openSnackbar("msg.taak.afgerond");
        this.init(taak, true);
      });
    }
  }

  onFormioFormSubmit(submission: any) {
    this.websocketService.suspendListener(this.taakListener);
    for (const key in submission.data) {
      if (key !== "submit" && key !== "save") {
        this.taak.taakdata[key] = submission.data[key];
      }
    }
    if (submission.state === "submitted") {
      this.takenService.complete(this.taak).subscribe((taak) => {
        this.utilService.openSnackbar("msg.taak.afgerond");
        this.init(taak, true);
      });
    } else {
      this.takenService.updateTaakdata(this.taak).subscribe((taak) => {
        this.utilService.openSnackbar("msg.taak.opgeslagen");
        this.init(taak, false);
        this.posts++;
      });
    }
  }

  editToewijzing(event: any) {
    if (
      event["medewerker-groep"].medewerker &&
      event["medewerker-groep"].medewerker.id === this.ingelogdeMedewerker.id &&
      this.taak.groep === event["medewerker-groep"].groep
    ) {
      this.assignToMe();
    } else {
      this.taak.groep = event["medewerker-groep"].groep;
      this.taak.behandelaar = event["medewerker-groep"].medewerker;
      const reden: string = event["reden"];
      this.websocketService.suspendListener(this.taakListener);
      this.takenService.toekennen(this.taak, reden).subscribe(() => {
        if (this.taak.behandelaar) {
          this.utilService.openSnackbar("msg.taak.toegekend", {
            behandelaar: this.taak.behandelaar.naam,
          });
        } else {
          this.utilService.openSnackbar("msg.vrijgegeven.taak");
        }
        this.init(this.taak, false);
      });
    }
  }

  private reloadTaak() {
    this.takenService.readTaak(this.taak.id).subscribe((taak) => {
      this.init(taak, true);
    });
  }

  private getIngelogdeMedewerker() {
    this.identityService.readLoggedInUser().subscribe((ingelogdeMedewerker) => {
      this.ingelogdeMedewerker = ingelogdeMedewerker;
    });
  }

  private assignToMe(): void {
    this.websocketService.suspendListener(this.taakListener);
    this.takenService
      .toekennenAanIngelogdeMedewerker(this.taak)
      .subscribe((taak) => {
        this.taak.behandelaar = taak.behandelaar;
        this.utilService.openSnackbar("msg.taak.toegekend", {
          behandelaar: taak.behandelaar.naam,
        });
        this.init(this.taak, false);
      });
  }

  updateTaakdocumenten(informatieobject: EnkelvoudigInformatieobject) {
    if (!this.taak.taakdocumenten) {
      this.taak.taakdocumenten = [];
    }

    this.taak.taakdocumenten.push(informatieobject.uuid);
    this.formulier.refreshTaakdocumentenEnBijlagen();
  }

  /**
   *  Zaak is nog niet geladen, beschikbare zaak-data uit de taak vast weergeven totdat de zaak is geladen
   */
  private createZaakFromTaak(taak: Taak): void {
    const zaak = new Zaak();
    zaak.identificatie = taak.zaakIdentificatie;
    zaak.uuid = taak.zaakUuid;
    zaak.zaaktype = new Zaaktype();
    zaak.zaaktype.omschrijving = taak.zaaktypeOmschrijving;
    this.zaak = zaak;
  }
}
