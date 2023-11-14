/*
 * SPDX-FileCopyrightText: 2022 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

/**
 * Klanten API
 * Een API om klanten te benaderen.  Een API om zowel klanten te registreren als op te vragen. Een klant is een natuurlijk persoon, niet-natuurlijk persoon (bedrijf) of vestiging waarbij het gaat om niet geverifieerde gegevens. De Klanten API kan zelfstandig of met andere API's samen werken om tot volledige functionaliteit te komen.  **Afhankelijkheden**  Deze API is afhankelijk van:  * Autorisaties API * Notificaties API * Zaken API *(optioneel)* * Documenten API *(optioneel)*  **Autorisatie**  Deze API vereist autorisatie. Je kan de [token-tool](https://zaken-auth.vng.cloud/) gebruiken om JWT-tokens te genereren.  ** Notificaties  Deze API publiceert notificaties op het kanaal `klanten`.  **Main resource**  `klant`    **Kenmerken**  * `subject_type`: Type van de `subject`.  **Resources en acties**   **Handige links**  * [Documentatie](https://zaakgerichtwerken.vng.cloud/standaard) * [Zaakgericht werken](https://zaakgerichtwerken.vng.cloud)
 * <p>
 * The version of the OpenAPI document: 1.0.0
 * Contact: standaarden.ondersteuning@vng.nl
 * <p>
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */

package net.atos.client.klanten.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.json.bind.annotation.JsonbProperty;


public class ValidatieFout {

    /**
     * URI referentie naar het type fout, bedoeld voor developers
     **/
    @JsonbProperty("type")
    private String type;

    /**
     * Systeemcode die het type fout aangeeft
     **/
    @JsonbProperty("code")
    private String code;

    /**
     * Generieke titel voor het type fout
     **/
    @JsonbProperty("title")
    private String title;

    /**
     * De HTTP status code
     **/
    @JsonbProperty("status")
    private Integer status;

    /**
     * Extra informatie bij de fout, indien beschikbaar
     **/
    @JsonbProperty("detail")
    private String detail;

    /**
     * URI met referentie naar dit specifiek voorkomen van de fout. Deze kan gebruikt worden in combinatie met server logs, bijvoorbeeld.
     **/
    @JsonbProperty("instance")
    private String instance;

    @JsonbProperty("invalidParams")
    private List<FieldValidationError> invalidParams = new ArrayList<>();

    /**
     * URI referentie naar het type fout, bedoeld voor developers
     * @return type
     **/
    public String getType() {
        return type;
    }

    /**
     * Set type
     **/
    public void setType(String type) {
        this.type = type;
    }

    public ValidatieFout type(String type) {
        this.type = type;
        return this;
    }

    /**
     * Systeemcode die het type fout aangeeft
     * @return code
     **/
    public String getCode() {
        return code;
    }

    /**
     * Set code
     **/
    public void setCode(String code) {
        this.code = code;
    }

    public ValidatieFout code(String code) {
        this.code = code;
        return this;
    }

    /**
     * Generieke titel voor het type fout
     * @return title
     **/
    public String getTitle() {
        return title;
    }

    /**
     * Set title
     **/
    public void setTitle(String title) {
        this.title = title;
    }

    public ValidatieFout title(String title) {
        this.title = title;
        return this;
    }

    /**
     * De HTTP status code
     * @return status
     **/
    public Integer getStatus() {
        return status;
    }

    /**
     * Set status
     **/
    public void setStatus(Integer status) {
        this.status = status;
    }

    public ValidatieFout status(Integer status) {
        this.status = status;
        return this;
    }

    /**
     * Extra informatie bij de fout, indien beschikbaar
     * @return detail
     **/
    public String getDetail() {
        return detail;
    }

    /**
     * Set detail
     **/
    public void setDetail(String detail) {
        this.detail = detail;
    }

    public ValidatieFout detail(String detail) {
        this.detail = detail;
        return this;
    }

    /**
     * URI met referentie naar dit specifiek voorkomen van de fout. Deze kan gebruikt worden in combinatie met server logs, bijvoorbeeld.
     * @return instance
     **/
    public String getInstance() {
        return instance;
    }

    /**
     * Set instance
     **/
    public void setInstance(String instance) {
        this.instance = instance;
    }

    public ValidatieFout instance(String instance) {
        this.instance = instance;
        return this;
    }

    /**
     * Get invalidParams
     * @return invalidParams
     **/
    public List<FieldValidationError> getInvalidParams() {
        return invalidParams;
    }

    /**
     * Set invalidParams
     **/
    public void setInvalidParams(List<FieldValidationError> invalidParams) {
        this.invalidParams = invalidParams;
    }

    public ValidatieFout invalidParams(List<FieldValidationError> invalidParams) {
        this.invalidParams = invalidParams;
        return this;
    }

    public ValidatieFout addInvalidParamsItem(FieldValidationError invalidParamsItem) {
        this.invalidParams.add(invalidParamsItem);
        return this;
    }


    /**
     * Create a string representation of this pojo.
     **/
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ValidatieFout {\n");

        sb.append("    type: ").append(toIndentedString(type)).append("\n");
        sb.append("    code: ").append(toIndentedString(code)).append("\n");
        sb.append("    title: ").append(toIndentedString(title)).append("\n");
        sb.append("    status: ").append(toIndentedString(status)).append("\n");
        sb.append("    detail: ").append(toIndentedString(detail)).append("\n");
        sb.append("    instance: ").append(toIndentedString(instance)).append("\n");
        sb.append("    invalidParams: ").append(toIndentedString(invalidParams)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private static String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}

