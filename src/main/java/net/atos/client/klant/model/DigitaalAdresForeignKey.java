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


public class DigitaalAdresForeignKey {

    /**
     * Unieke (technische) identificatiecode van het digitaal adres.
     */
    @JsonbProperty("uuid")
    private UUID uuid;

    /**
     * De unieke URL van dit digitaal adres binnen deze API.
     */
    @JsonbProperty("url")
    private URI url;

    public DigitaalAdresForeignKey() {
    }

    @JsonbCreator
    public DigitaalAdresForeignKey(
            @JsonbProperty(value = "url") URI url
    ) {
        this.url = url;
    }

    /**
     * Unieke (technische) identificatiecode van het digitaal adres.
     * 
     * @return uuid
     **/
    public UUID getUuid() {
        return uuid;
    }

    /**
     * Set uuid
     */
    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public DigitaalAdresForeignKey uuid(UUID uuid) {
        this.uuid = uuid;
        return this;
    }

    /**
     * De unieke URL van dit digitaal adres binnen deze API.
     * 
     * @return url
     **/
    public URI getUrl() {
        return url;
    }


    /**
     * Create a string representation of this pojo.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class DigitaalAdresForeignKey {\n");

        sb.append("    uuid: ").append(toIndentedString(uuid)).append("\n");
        sb.append("    url: ").append(toIndentedString(url)).append("\n");
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
