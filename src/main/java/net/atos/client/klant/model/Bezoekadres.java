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

import jakarta.json.bind.annotation.JsonbProperty;

/**
 * Generate a serializer out of a GegevensGroepType. Usage:: >>> class VerlengingSerializer(GegevensGroepSerializer): ... class Meta: ...
 * model = Zaak ... gegevensgroep = 'verlenging' >>> Where ``Zaak.verlenging`` is a :class:``GegevensGroepType``.
 */

public class Bezoekadres {

    /**
     * Identificatie van het adres bij de Basisregistratie Adressen en Gebouwen.
     */
    @JsonbProperty("nummeraanduidingId")
    private String nummeraanduidingId;

    /**
     * Eerste deel van het adres dat niet voorkomt in de Basisregistratie Adressen en Gebouwen.
     */
    @JsonbProperty("adresregel1")
    private String adresregel1;

    /**
     * Tweede deel van het adres dat niet voorkomt in de Basisregistratie Adressen en Gebouwen.
     */
    @JsonbProperty("adresregel2")
    private String adresregel2;

    /**
     * Derde deel van het adres dat niet voorkomt in de Basisregistratie Adressen en Gebouwen.
     */
    @JsonbProperty("adresregel3")
    private String adresregel3;

    /**
     * Een code, opgenomen in Tabel 34, Landentabel, die het land (buiten Nederland) aangeeft alwaar de ingeschrevene verblijft.
     */
    @JsonbProperty("land")
    private String land;

    /**
     * Identificatie van het adres bij de Basisregistratie Adressen en Gebouwen.
     * 
     * @return nummeraanduidingId
     **/
    public String getNummeraanduidingId() {
        return nummeraanduidingId;
    }

    /**
     * Set nummeraanduidingId
     */
    public void setNummeraanduidingId(String nummeraanduidingId) {
        this.nummeraanduidingId = nummeraanduidingId;
    }

    public Bezoekadres nummeraanduidingId(String nummeraanduidingId) {
        this.nummeraanduidingId = nummeraanduidingId;
        return this;
    }

    /**
     * Eerste deel van het adres dat niet voorkomt in de Basisregistratie Adressen en Gebouwen.
     * 
     * @return adresregel1
     **/
    public String getAdresregel1() {
        return adresregel1;
    }

    /**
     * Set adresregel1
     */
    public void setAdresregel1(String adresregel1) {
        this.adresregel1 = adresregel1;
    }

    public Bezoekadres adresregel1(String adresregel1) {
        this.adresregel1 = adresregel1;
        return this;
    }

    /**
     * Tweede deel van het adres dat niet voorkomt in de Basisregistratie Adressen en Gebouwen.
     * 
     * @return adresregel2
     **/
    public String getAdresregel2() {
        return adresregel2;
    }

    /**
     * Set adresregel2
     */
    public void setAdresregel2(String adresregel2) {
        this.adresregel2 = adresregel2;
    }

    public Bezoekadres adresregel2(String adresregel2) {
        this.adresregel2 = adresregel2;
        return this;
    }

    /**
     * Derde deel van het adres dat niet voorkomt in de Basisregistratie Adressen en Gebouwen.
     * 
     * @return adresregel3
     **/
    public String getAdresregel3() {
        return adresregel3;
    }

    /**
     * Set adresregel3
     */
    public void setAdresregel3(String adresregel3) {
        this.adresregel3 = adresregel3;
    }

    public Bezoekadres adresregel3(String adresregel3) {
        this.adresregel3 = adresregel3;
        return this;
    }

    /**
     * Een code, opgenomen in Tabel 34, Landentabel, die het land (buiten Nederland) aangeeft alwaar de ingeschrevene verblijft.
     * 
     * @return land
     **/
    public String getLand() {
        return land;
    }

    /**
     * Set land
     */
    public void setLand(String land) {
        this.land = land;
    }

    public Bezoekadres land(String land) {
        this.land = land;
        return this;
    }


    /**
     * Create a string representation of this pojo.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Bezoekadres {\n");

        sb.append("    nummeraanduidingId: ").append(toIndentedString(nummeraanduidingId)).append("\n");
        sb.append("    adresregel1: ").append(toIndentedString(adresregel1)).append("\n");
        sb.append("    adresregel2: ").append(toIndentedString(adresregel2)).append("\n");
        sb.append("    adresregel3: ").append(toIndentedString(adresregel3)).append("\n");
        sb.append("    land: ").append(toIndentedString(land)).append("\n");
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
