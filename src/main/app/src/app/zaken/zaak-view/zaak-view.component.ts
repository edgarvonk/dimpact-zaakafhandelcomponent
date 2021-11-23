/*
 * SPDX-FileCopyrightText: 2021 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AfterViewInit, Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {Taak} from '../../taken/model/taak';
import {UtilService} from '../../core/service/util.service';
import {MenuItem} from '../../shared/side-nav/menu-item/menu-item';
import {InformatieObjectenService} from '../../informatie-objecten/informatie-objecten.service';
import {TakenService} from '../../taken/taken.service';
import {EnkelvoudigInformatieObject} from '../../informatie-objecten/model/enkelvoudig-informatie-object';
import {Zaak} from '../model/zaak';
import {PlanItemsService} from '../../plan-items/plan-items.service';
import {PlanItem} from '../../plan-items/model/plan-item';
import {PlanItemType} from '../../plan-items/model/plan-item-type.enum';
import {MatSort} from '@angular/material/sort';
import {MatTableDataSource} from '@angular/material/table';
import {HeaderMenuItem} from '../../shared/side-nav/menu-item/header-menu-item';
import {LinkMenuTitem} from '../../shared/side-nav/menu-item/link-menu-titem';
import {MatSidenavContainer} from '@angular/material/sidenav';
import {Store} from '@ngrx/store';
import {State} from '../../state/app.state';
import {AbstractView} from '../../shared/abstract-view/abstract-view';
import {ButtonMenuItem} from '../../shared/side-nav/menu-item/button-menu-item';
import {ZakenService} from '../zaken.service';
import {WebsocketService} from '../../core/websocket/websocket.service';
import {Opcode} from '../../core/websocket/model/opcode';
import {ObjectType} from '../../core/websocket/model/object-type';
import {NotitieType} from '../../shared/notities/model/notitietype.enum';
import {SessionStorageService} from '../../shared/storage/session-storage.service';
import {ZaakRechten} from '../model/zaak-rechten';
import {TaakRechten} from '../../taken/model/taak-rechten';

@Component({
    templateUrl: './zaak-view.component.html',
    styleUrls: ['./zaak-view.component.less']
})
export class ZaakViewComponent extends AbstractView implements OnInit, AfterViewInit, OnDestroy {

    zaak: Zaak;
    menu: MenuItem[];
    takenDataSource: MatTableDataSource<Taak> = new MatTableDataSource<Taak>();
    toonAfgerondeTaken: boolean = false;

    takenColumnsToDisplay: string[] = ['naam', 'status', 'creatiedatumTijd', 'streefdatum', 'groep', 'behandelaar', 'id'];
    enkelvoudigInformatieObjecten: EnkelvoudigInformatieObject[] = [];
    gerelateerdeZaakColumns: string[] = ['identificatie', 'relatieType', 'omschrijving', 'startdatum', 'einddatum', 'uuid'];

    notitieType = NotitieType.ZAAK;

    get zaakRechten(): typeof ZaakRechten {
        return ZaakRechten;
    };

    get taakRechten(): typeof TaakRechten {
        return TaakRechten;
    };

    takenFilter: any = {};

    @ViewChild(MatSidenavContainer) sideNavContainer: MatSidenavContainer;
    @ViewChild(MatSort) sort: MatSort;

    constructor(store: Store<State>,
                private informatieObjectenService: InformatieObjectenService,
                private takenService: TakenService,
                private zakenService: ZakenService,
                private planItemsService: PlanItemsService,
                private route: ActivatedRoute,
                public utilService: UtilService,
                public websocketService: WebsocketService,
                private sessionStorageService: SessionStorageService) {
        super(store, utilService);
    }

    ngOnInit(): void {
        this.subscriptions$.push(this.route.data.subscribe(data => {
            this.init(data['zaak']);
            this.websocketService.addListener(Opcode.ANY, ObjectType.ZAAK, this.zaak.uuid,
                () => this.updateZaak());
            this.websocketService.addListener(Opcode.UPDATED, ObjectType.ZAAK_ROLLEN, this.zaak.uuid,
                () => this.updateZaak());
            this.websocketService.addListener(Opcode.UPDATED, ObjectType.ZAAK_TAKEN, this.zaak.uuid,
                () => this.loadTaken());
            this.websocketService.addListener(Opcode.UPDATED, ObjectType.ZAAK_INFORMATIEOBJECTEN, this.zaak.uuid,
                () => this.loadInformatieObjecten());

            this.utilService.setTitle('title.zaak', {zaak: this.zaak.identificatie});

            this.loadTaken();
            this.loadInformatieObjecten();
        }));

        this.takenDataSource.filterPredicate = (data: Taak, filter: string): boolean => {
            return (!this.toonAfgerondeTaken ? data.status !== filter['status'] : true);
        };

        this.toonAfgerondeTaken = this.sessionStorageService.getSessionStorage('toonAfgerondeTaken');

    }

    init(zaak: Zaak): void {
        this.zaak = zaak;
        this.setupMenu();
    }

    ngAfterViewInit() {
        super.ngAfterViewInit();
        this.takenDataSource.sortingDataAccessor = (item, property) => {
            switch (property) {
                case 'groep':
                    return item.groep.naam;
                case 'behandelaar' :
                    return item.behandelaar.naam;
                default:
                    return item[property];
            }
        };
        this.takenDataSource.sort = this.sort;
    }

    ngOnDestroy(): void {
        super.ngOnDestroy();
        this.websocketService.removeListeners(Opcode.ANY, ObjectType.ZAAK, this.zaak.uuid);
        this.websocketService.removeListeners(Opcode.UPDATED, ObjectType.ZAAK_ROLLEN, this.zaak.uuid);
        this.websocketService.removeListeners(Opcode.UPDATED, ObjectType.ZAAK_TAKEN, this.zaak.uuid);
        this.websocketService.removeListeners(Opcode.UPDATED, ObjectType.ZAAK_INFORMATIEOBJECTEN, this.zaak.uuid);
    }

    private createMenuItem(planItem: PlanItem): MenuItem {
        let icon: string;
        switch (planItem.type) {
            case PlanItemType.HumanTask:
                icon = 'assignment';
                break;
            case PlanItemType.UserEventListener:
                icon = 'fact_check';
                break;
            case PlanItemType.ProcessTask:
                icon = 'launch';
                break;
        }
        return new LinkMenuTitem(planItem.naam, `/plan-items/${planItem.id}/do`, icon);
    }

    private setupMenu(): void {
        this.menu = [new HeaderMenuItem('zaak')];

        if (this.zaak.rechten[this.zaakRechten.BEHANDELEN]) {
            this.menu.push(new LinkMenuTitem('actie.document.aanmaken', `/informatie-objecten/create/${this.zaak.uuid}`, 'upload_file'));

            this.planItemsService.getPlanItemsForZaak(this.zaak.uuid).subscribe(planItems => {
                if (planItems.length > 0) {
                    this.menu.push(new HeaderMenuItem('planItems'));
                }
                this.menu = this.menu.concat(planItems.map(planItem => this.createMenuItem(planItem)));
            });
        }

        if (this.zaak.rechten[this.zaakRechten.TOEKENNEN]) {
            this.menu.push(new LinkMenuTitem('actie.toekennen', `/zaken/${this.zaak.uuid}/toekennen`, 'assignment_ind'));
        }

        if (this.zaak.rechten[this.zaakRechten.VRIJGEVEN]) {
            this.menu.push(new ButtonMenuItem('actie.vrijgeven', this.vrijgeven, 'assignment_return'));
        }
    }

    editZaak(value: string, field: string): void {
        const patchData: Zaak = new Zaak();
        patchData[field] = value;
        this.zakenService.updateZaak(this.zaak.uuid, patchData).subscribe(updatedZaak => {
            this.init(updatedZaak);
        });
    }

    updateZaak(): void {
        this.zakenService.getZaak(this.zaak.uuid).subscribe(zaak => {
            this.init(zaak);
        });
    }

    private loadInformatieObjecten(): void {
        this.informatieObjectenService.getEnkelvoudigInformatieObjectenVoorZaak(this.zaak.uuid).subscribe(objecten => {
            this.enkelvoudigInformatieObjecten = objecten;
        });
    }

    private loadTaken(): void {
        this.takenService.listTakenVoorZaak(this.zaak.uuid).subscribe(taken => {
            taken = taken.sort((a, b) => a.streefdatum?.localeCompare(b.streefdatum) ||
                a.creatiedatumTijd?.localeCompare(b.creatiedatumTijd));
            this.takenDataSource.data = taken;
            this.filterTakenOpStatus();
        });
    }

    vrijgeven = (): void => {
        this.zaak.behandelaar = null;
        this.zakenService.toekennen(this.zaak).subscribe((zaak) => {
            this.utilService.openSnackbar('msg.zaak.vrijgegeven');
            this.init(zaak);
        });
    };

    taakToekennenAanIngelogdeMedewerker(taak: Taak) {
        this.takenService.assignToLoggedOnUser(taak).subscribe(taakResponse => {
            this.utilService.openSnackbar('msg.taak.toegekend', {behandelaar: taakResponse.behandelaar.naam});
            taak.behandelaar = taakResponse.behandelaar;
            taak.status = taakResponse.status;
            taak.rechten = taakResponse.rechten;
        });
    }

    filterTakenOpStatus() {
        if (!this.toonAfgerondeTaken) {
            this.takenFilter['status'] = 'AFGEROND';
        }

        this.takenDataSource.filter = this.takenFilter;
        this.sessionStorageService.setSessionStorage('toonAfgerondeTaken', this.toonAfgerondeTaken);
    }
}
