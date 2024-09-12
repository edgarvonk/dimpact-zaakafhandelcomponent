/*
 * SPDX-FileCopyrightText: 2021 Atos, 2024 Lifely
 * SPDX-License-Identifier: EUPL-1.2+
 */

import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable, throwError } from "rxjs";
import { catchError } from "rxjs/operators";
import { FoutAfhandelingService } from "../fout-afhandeling/fout-afhandeling.service";
import { Resultaattype } from "../zaken/model/resultaattype";
import { CaseDefinition } from "./model/case-definition";
import { FormulierDefinitie } from "./model/formulier-definitie";
import { ReplyTo } from "./model/replyto";
import { ZaakafhandelParameters } from "./model/zaakafhandel-parameters";
import { ZaakbeeindigReden } from "./model/zaakbeeindig-reden";

@Injectable({
  providedIn: "root",
})
export class ZaakafhandelParametersService {
  constructor(
    private http: HttpClient,
    private foutAfhandelingService: FoutAfhandelingService,
  ) {}

  private basepath = "/rest/zaakafhandelparameters";

  listZaakafhandelParameters(): Observable<ZaakafhandelParameters[]> {
    return this.http
      .get<ZaakafhandelParameters[]>(`${this.basepath}`)
      .pipe(
        catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
      );
  }

  readZaakafhandelparameters(
    zaaktypeUuid: string,
  ): Observable<ZaakafhandelParameters> {
    return this.http
      .get<ZaakafhandelParameters>(`${this.basepath}/${zaaktypeUuid}`)
      .pipe(
        catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
      );
  }

  listZaakbeeindigRedenen(): Observable<ZaakbeeindigReden[]> {
    return this.http
      .get<ZaakbeeindigReden[]>(`${this.basepath}/zaakbeeindigredenen`)
      .pipe(
        catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
      );
  }

  listZaakbeeindigRedenenForZaaktype(
    zaaktypeUuid: string,
  ): Observable<ZaakbeeindigReden[]> {
    return this.http
      .get<
        ZaakbeeindigReden[]
      >(`${this.basepath}/zaakbeeindigRedenen/${zaaktypeUuid}`)
      .pipe(
        catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
      );
  }

  listResultaattypes(zaaktypeUuid: string): Observable<Resultaattype[]> {
    return this.http
      .get<Resultaattype[]>(`${this.basepath}/resultaattypes/${zaaktypeUuid}`)
      .pipe(
        catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
      );
  }

  listCaseDefinitions(): Observable<CaseDefinition[]> {
    return this.http
      .get<CaseDefinition[]>(`${this.basepath}/case-definitions`)
      .pipe(
        catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
      );
  }

  readCaseDefinition(key: string): Observable<CaseDefinition> {
    return this.http
      .get<CaseDefinition>(`${this.basepath}/case-definitions/${key}`)
      .pipe(
        catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
      );
  }

  updateZaakafhandelparameters(
    zaakafhandelparameters: ZaakafhandelParameters,
  ): Observable<ZaakafhandelParameters> {
    return this.http
      .put<ZaakafhandelParameters>(`${this.basepath}`, zaakafhandelparameters)
      .pipe(
        catchError((err) => {
          this.foutAfhandelingService.foutAfhandelen(err);
          return throwError(() => err);
        }),
      );
  }

  listFormulierDefinities(): Observable<FormulierDefinitie[]> {
    return this.http
      .get<FormulierDefinitie[]>(`${this.basepath}/formulierdefinities`)
      .pipe(
        catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
      );
  }

  listReplyTos(): Observable<ReplyTo[]> {
    return this.http
      .get<ReplyTo[]>(`${this.basepath}/replyTo`)
      .pipe(
        catchError((err) => this.foutAfhandelingService.foutAfhandelen(err)),
      );
  }
}
