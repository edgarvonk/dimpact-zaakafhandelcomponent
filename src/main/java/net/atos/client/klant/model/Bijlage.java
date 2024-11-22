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

import java.net.URI;
import java.util.UUID;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;

/**
 * Set gegevensgroepdata from validated nested data. Usage: include the mixin on the ModelSerializer that has gegevensgroepen.
 */

public class Bijlage {

    /**
     * Unieke (technische) identificatiecode van het inhoudsdeel.
     */
    @JsonbProperty("uuid")
    private UUID uuid;

    /**
     * De unieke URL van dit klantcontact binnen deze API.
     */
    @JsonbProperty("url")
    private URI url;

    /**
     * 'Klantcontact' ging over 'Onderwerpobject'
     */
    @JsonbProperty("wasBijlageVanKlantcontact")
    private KlantcontactForeignKey wasBijlageVanKlantcontact;

    /**
     * Gegevens die een inhoudsobject in een extern register uniek identificeren.
     */
    @JsonbProperty("bijlageidentificator")
    private BijlageIdentificator bijlageidentificator;

    public Bijlage() {
    }

    @JsonbCreator
    public Bijlage(
            @JsonbProperty(value = "uuid") UUID uuid,
            @JsonbProperty(value = "url") URI url
    ) {
        this.uuid = uuid;
        this.url = url;
    }

    /**
     * Unieke (technische) identificatiecode van het inhoudsdeel.
     * 
     * @return uuid
     **/
    public UUID getUuid() {
        return uuid;
    }


    /**
     * De unieke URL van dit klantcontact binnen deze API.
     * 
     * @return url
     **/
    public URI getUrl() {
        return url;
    }


    /**
     * &#39;Klantcontact&#39; ging over &#39;Onderwerpobject&#39;
     * 
     * @return wasBijlageVanKlantcontact
     **/
    public KlantcontactForeignKey getWasBijlageVanKlantcontact() {
        return wasBijlageVanKlantcontact;
    }

    /**
     * Set wasBijlageVanKlantcontact
     */
    public void setWasBijlageVanKlantcontact(KlantcontactForeignKey wasBijlageVanKlantcontact) {
        this.wasBijlageVanKlantcontact = wasBijlageVanKlantcontact;
    }

    public Bijlage wasBijlageVanKlantcontact(KlantcontactForeignKey wasBijlageVanKlantcontact) {
        this.wasBijlageVanKlantcontact = wasBijlageVanKlantcontact;
        return this;
    }

    /**
     * Gegevens die een inhoudsobject in een extern register uniek identificeren.
     * 
     * @return bijlageidentificator
     **/
    public BijlageIdentificator getBijlageidentificator() {
        return bijlageidentificator;
    }

    /**
     * Set bijlageidentificator
     */
    public void setBijlageidentificator(BijlageIdentificator bijlageidentificator) {
        this.bijlageidentificator = bijlageidentificator;
    }

    public Bijlage bijlageidentificator(BijlageIdentificator bijlageidentificator) {
        this.bijlageidentificator = bijlageidentificator;
        return this;
    }


    /**
     * Create a string representation of this pojo.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Bijlage {\n");

        sb.append("    uuid: ").append(toIndentedString(uuid)).append("\n");
        sb.append("    url: ").append(toIndentedString(url)).append("\n");
        sb.append("    wasBijlageVanKlantcontact: ").append(toIndentedString(wasBijlageVanKlantcontact)).append("\n");
        sb.append("    bijlageidentificator: ").append(toIndentedString(bijlageidentificator)).append("\n");
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
