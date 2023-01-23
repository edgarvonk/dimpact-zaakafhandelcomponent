/*
 * SPDX-FileCopyrightText: 2021 - 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';

import {detailExpand} from '../../shared/animations/animations';

import {ColumnPickerValue} from '../../shared/dynamic-table/column-picker/column-picker-value';
import {UtilService} from '../../core/service/util.service';
import {ZakenService} from '../zaken.service';
import {MatLegacyTable as MatTable} from '@angular/material/legacy-table';
import {ZaakZoekObject} from '../../zoeken/model/zaken/zaak-zoek-object';
import {MatLegacyPaginator as MatPaginator} from '@angular/material/legacy-paginator';
import {MatSort} from '@angular/material/sort';
import {ZoekenService} from '../../zoeken/zoeken.service';
import {TextIcon} from '../../shared/edit/text-icon';
import {Conditionals} from '../../shared/edit/conditional-fn';
import {ZakenMijnDatasource} from './zaken-mijn-datasource';
import {SorteerVeld} from '../../zoeken/model/sorteer-veld';
import {ZoekenColumn} from '../../shared/dynamic-table/model/zoeken-column';
import {IndicatiesLayout} from '../../shared/indicaties/indicaties.component';

@Component({
    templateUrl: './zaken-mijn.component.html',
    styleUrls: ['./zaken-mijn.component.less'],
    animations: [detailExpand]
})
export class ZakenMijnComponent implements AfterViewInit, OnInit {
    readonly indicatiesLayout = IndicatiesLayout;
    dataSource: ZakenMijnDatasource;
    @ViewChild(MatPaginator) paginator: MatPaginator;
    @ViewChild(MatSort) sort: MatSort;
    @ViewChild(MatTable) table: MatTable<ZaakZoekObject>;
    expandedRow: ZaakZoekObject | null;
    readonly zoekenColumn = ZoekenColumn;
    sorteerVeld = SorteerVeld;

    einddatumGeplandIcon: TextIcon = new TextIcon(Conditionals.isAfterDate(), 'report_problem',
        'warningVerlopen_icon', 'msg.datum.overschreden', 'warning');
    uiterlijkeEinddatumAfdoeningIcon: TextIcon = new TextIcon(Conditionals.isAfterDate(), 'report_problem',
        'errorVerlopen_icon', 'msg.datum.overschreden', 'error');

    constructor(private zakenService: ZakenService, private zoekenService: ZoekenService, public utilService: UtilService) {
        this.dataSource = new ZakenMijnDatasource(this.zoekenService, this.utilService);
    }

    ngOnInit(): void {
        this.utilService.setTitle('title.zaken.mijn');
        this.dataSource.initColumns(this.defaultColumns());
    }

    defaultColumns(): Map<ZoekenColumn, ColumnPickerValue> {
        return new Map([
            [ZoekenColumn.ZAAK_DOT_IDENTIFICATIE, ColumnPickerValue.VISIBLE],
            [ZoekenColumn.STATUS, ColumnPickerValue.VISIBLE],
            [ZoekenColumn.ZAAKTYPE, ColumnPickerValue.VISIBLE],
            [ZoekenColumn.OMSCHRIJVING, ColumnPickerValue.VISIBLE],
            [ZoekenColumn.GROEP, ColumnPickerValue.HIDDEN],
            [ZoekenColumn.STARTDATUM, ColumnPickerValue.VISIBLE],
            [ZoekenColumn.OPENSTAANDE_TAKEN, ColumnPickerValue.VISIBLE],
            [ZoekenColumn.EINDDATUM, ColumnPickerValue.HIDDEN],
            [ZoekenColumn.EINDDATUM_GEPLAND, ColumnPickerValue.HIDDEN],
            [ZoekenColumn.DAGEN_TOT_STREEFDATUM, ColumnPickerValue.VISIBLE],
            [ZoekenColumn.UITERLIJKE_EINDDATUM_AFDOENING, ColumnPickerValue.HIDDEN],
            [ZoekenColumn.DAGEN_TOT_FATALEDATUM, ColumnPickerValue.VISIBLE],
            [ZoekenColumn.INDICATIES, ColumnPickerValue.VISIBLE],
            [ZoekenColumn.TOELICHTING, ColumnPickerValue.HIDDEN],
            [ZoekenColumn.URL, ColumnPickerValue.STICKY]
        ]);
    }

    ngAfterViewInit(): void {
        this.dataSource.setViewChilds(this.paginator, this.sort);
        this.table.dataSource = this.dataSource;
    }

    isAfterDate(datum): boolean {
        return Conditionals.isOverschreden(datum);
    }

    resetColumns(): void {
        this.dataSource.resetColumns();
    }

    filtersChange(): void {
        this.dataSource.filtersChanged();
    }
}
