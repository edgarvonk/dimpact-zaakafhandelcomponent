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

import java.util.List;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;

/**
 * Display details of the linked resources requested in the `expand` parameter
 */

public class ExpandKlantcontactAllOfExpand {

    /**
     * Persoon of organisatie die betrokken was bij een klantcontact.
     */
    @JsonbProperty("had_betrokkenen")
    private List<Betrokkene> hadBetrokkenen;

    /**
     * Klantcontact dat leidde tot een interne taak.
     */
    @JsonbProperty("leidde_tot_interne_taken")
    private InterneTaak leiddeTotInterneTaken;

    /**
     * Onderwerpobject dat tijdens een klantcontact aan de orde was.
     */
    @JsonbProperty("ging_over_onderwerpobjecten")
    private Onderwerpobject gingOverOnderwerpobjecten;

    /**
     * Bijlage die (een deel van) de inhoud van het klantcontact beschrijft.
     */
    @JsonbProperty("omvatte_bijlagen")
    private Bijlage omvatteBijlagen;

    public ExpandKlantcontactAllOfExpand() {
    }

    @JsonbCreator
    public ExpandKlantcontactAllOfExpand(
            @JsonbProperty(value = "had_betrokkenen", nillable = true) List<Betrokkene> hadBetrokkenen,
            @JsonbProperty(value = "leidde_tot_interne_taken", nillable = true) InterneTaak leiddeTotInterneTaken,
            @JsonbProperty(value = "ging_over_onderwerpobjecten", nillable = true) Onderwerpobject gingOverOnderwerpobjecten,
            @JsonbProperty(value = "omvatte_bijlagen", nillable = true) Bijlage omvatteBijlagen
    ) {
        this.hadBetrokkenen = hadBetrokkenen;
        this.leiddeTotInterneTaken = leiddeTotInterneTaken;
        this.gingOverOnderwerpobjecten = gingOverOnderwerpobjecten;
        this.omvatteBijlagen = omvatteBijlagen;
    }

    /**
     * Persoon of organisatie die betrokken was bij een klantcontact.
     * 
     * @return hadBetrokkenen
     **/
    public List<Betrokkene> getHadBetrokkenen() {
        return hadBetrokkenen;
    }


    /**
     * Klantcontact dat leidde tot een interne taak.
     * 
     * @return leiddeTotInterneTaken
     **/
    public InterneTaak getLeiddeTotInterneTaken() {
        return leiddeTotInterneTaken;
    }


    /**
     * Onderwerpobject dat tijdens een klantcontact aan de orde was.
     * 
     * @return gingOverOnderwerpobjecten
     **/
    public Onderwerpobject getGingOverOnderwerpobjecten() {
        return gingOverOnderwerpobjecten;
    }


    /**
     * Bijlage die (een deel van) de inhoud van het klantcontact beschrijft.
     * 
     * @return omvatteBijlagen
     **/
    public Bijlage getOmvatteBijlagen() {
        return omvatteBijlagen;
    }


    /**
     * Create a string representation of this pojo.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ExpandKlantcontactAllOfExpand {\n");

        sb.append("    hadBetrokkenen: ").append(toIndentedString(hadBetrokkenen)).append("\n");
        sb.append("    leiddeTotInterneTaken: ").append(toIndentedString(leiddeTotInterneTaken)).append("\n");
        sb.append("    gingOverOnderwerpobjecten: ").append(toIndentedString(gingOverOnderwerpobjecten)).append("\n");
        sb.append("    omvatteBijlagen: ").append(toIndentedString(omvatteBijlagen)).append("\n");
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
