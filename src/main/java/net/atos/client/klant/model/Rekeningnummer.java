/*
 * SPDX-FileCopyrightText: 2024 Lifely
 * SPDX-License-Identifier: EUPL-1.2+
 */

/**
 * klantinteracties
 * Description WIP.
 *
 * The version of the OpenAPI document: 0.0.3
 * Contact: standaarden.ondersteuning@vng.nl
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */

package net.atos.client.klant.model;

import java.util.UUID;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;


public class Rekeningnummer {

    /**
     * Unieke (technische) identificatiecode van de interne taak.
     */
    @JsonbProperty("uuid")
    private UUID uuid;

    /**
     * Rekeningnummer van een partij
     */
    @JsonbProperty("partij")
    private PartijForeignKey partij;

    /**
     * Het internationaal bankrekeningnummer, zoals dat door een bankinstelling als identificator aan een overeenkomst tussen de bank en een
     * of meer subjecten wordt toegekend, op basis waarvan het SUBJECT in de regel internationaal financieel communiceert.
     */
    @JsonbProperty("iban")
    private String iban;

    /**
     * De unieke code van de bankinstelling waar het SUBJECT het bankrekeningnummer heeft waarmee het subject in de regel internationaal
     * financieel communiceert.
     */
    @JsonbProperty("bic")
    private String bic;

    public Rekeningnummer() {
    }

    @JsonbCreator
    public Rekeningnummer(
            @JsonbProperty(value = "uuid") UUID uuid
    ) {
        this.uuid = uuid;
    }

    /**
     * Unieke (technische) identificatiecode van de interne taak.
     * 
     * @return uuid
     **/
    public UUID getUuid() {
        return uuid;
    }


    /**
     * Rekeningnummer van een partij
     * 
     * @return partij
     **/
    public PartijForeignKey getPartij() {
        return partij;
    }

    /**
     * Set partij
     */
    public void setPartij(PartijForeignKey partij) {
        this.partij = partij;
    }

    public Rekeningnummer partij(PartijForeignKey partij) {
        this.partij = partij;
        return this;
    }

    /**
     * Het internationaal bankrekeningnummer, zoals dat door een bankinstelling als identificator aan een overeenkomst tussen de bank en een
     * of meer subjecten wordt toegekend, op basis waarvan het SUBJECT in de regel internationaal financieel communiceert.
     * 
     * @return iban
     **/
    public String getIban() {
        return iban;
    }

    /**
     * Set iban
     */
    public void setIban(String iban) {
        this.iban = iban;
    }

    public Rekeningnummer iban(String iban) {
        this.iban = iban;
        return this;
    }

    /**
     * De unieke code van de bankinstelling waar het SUBJECT het bankrekeningnummer heeft waarmee het subject in de regel internationaal
     * financieel communiceert.
     * 
     * @return bic
     **/
    public String getBic() {
        return bic;
    }

    /**
     * Set bic
     */
    public void setBic(String bic) {
        this.bic = bic;
    }

    public Rekeningnummer bic(String bic) {
        this.bic = bic;
        return this;
    }


    /**
     * Create a string representation of this pojo.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Rekeningnummer {\n");

        sb.append("    uuid: ").append(toIndentedString(uuid)).append("\n");
        sb.append("    partij: ").append(toIndentedString(partij)).append("\n");
        sb.append("    iban: ").append(toIndentedString(iban)).append("\n");
        sb.append("    bic: ").append(toIndentedString(bic)).append("\n");
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
