/**
 * Contactmomenten API
 * Een API om contactmomenten met klanten te registreren of op te vragen.  **Afhankelijkheden**  Deze API is afhankelijk van:  * Autorisaties API * Notificaties API * Klanten API * Zaken API *(optioneel)* * Verzoeken API *(optioneel)* * Documenten API *(optioneel)*  **Autorisatie**  Deze API vereist autorisatie. Je kan de [token-tool](https://zaken-auth.vng.cloud/) gebruiken om JWT-tokens te genereren.  ** Notificaties  Deze API publiceert notificaties op het kanaal `contactmomenten`.  **Main resource**  `contactmoment`    **Kenmerken**  * `bronorganisatie`: Het RSIN van de Niet-natuurlijk persoon zijnde de organisatie die de klantinteractie heeft gecreeerd. Dit moet een geldig RSIN zijn van 9 nummers en voldoen aan https://nl.wikipedia.org/wiki/Burgerservicenummer#11-proef * `kanaal`: Het communicatiekanaal waarlangs het CONTACTMOMENT gevoerd wordt  **Resources en acties**   **Handige links**  * [Documentatie](https://zaakgerichtwerken.vng.cloud/standaard) * [Zaakgericht werken](https://zaakgerichtwerken.vng.cloud)
 *
 * The version of the OpenAPI document: 1.0.0
 * Contact: standaarden.ondersteuning@vng.nl
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */

package net.atos.client.contactmomenten.model;

import java.lang.reflect.Type;
import java.net.URI;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.json.bind.annotation.JsonbTypeDeserializer;
import jakarta.json.bind.annotation.JsonbTypeSerializer;
import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.bind.serializer.JsonbSerializer;
import jakarta.json.bind.serializer.SerializationContext;
import jakarta.json.stream.JsonGenerator;
import jakarta.json.stream.JsonParser;


public class KlantContactMoment  {

 /**
   * URL-referentie naar dit object. Dit is de unieke identificatie en locatie van dit object.
  **/
  @JsonbProperty("url")
  private URI url;

 /**
   * URL-referentie naar het CONTACTMOMENT.
  **/
  @JsonbProperty("contactmoment")
  private URI contactmoment;

 /**
   * URL-referentie naar de KLANT.
  **/
  @JsonbProperty("klant")
  private URI klant;

  @JsonbTypeSerializer(RolEnum.Serializer.class)
  @JsonbTypeDeserializer(RolEnum.Deserializer.class)
  public enum RolEnum {

    BELANGHEBBENDE(String.valueOf("belanghebbende")), GESPREKSPARTNER(String.valueOf("gesprekspartner"));


    String value;

    RolEnum (String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    public static final class Deserializer implements JsonbDeserializer<RolEnum> {
        @Override
        public RolEnum deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
            for (RolEnum b : RolEnum.values()) {
                if (String.valueOf(b.value).equals(parser.getString())) {
                    return b;
                }
            }
            throw new IllegalArgumentException("Unexpected value '" + parser.getString() + "'");
        }
    }

    public static final class Serializer implements JsonbSerializer<RolEnum> {
        @Override
        public void serialize(RolEnum obj, JsonGenerator generator, SerializationContext ctx) {
            generator.write(obj.value);
        }
    }
  }

 /**
   * De rol van de KLANT in het CONTACTMOMENT. Indien de KLANT zowel gesprekspartner als belanghebbende is, dan worden er twee KLANTCONTACTMOMENTen aangemaakt.
  **/
  @JsonbProperty("rol")
  private RolEnum rol;

  public KlantContactMoment() {
  }

 @JsonbCreator
  public KlantContactMoment(
    @JsonbProperty(value = "url", nillable = true) URI url
  ) {
    this.url = url;
  }

 /**
   * URL-referentie naar dit object. Dit is de unieke identificatie en locatie van dit object.
   * @return url
  **/
  public URI getUrl() {
    return url;
  }


 /**
   * URL-referentie naar het CONTACTMOMENT.
   * @return contactmoment
  **/
  public URI getContactmoment() {
    return contactmoment;
  }

  /**
    * Set contactmoment
  **/
  public void setContactmoment(URI contactmoment) {
    this.contactmoment = contactmoment;
  }

  public KlantContactMoment contactmoment(URI contactmoment) {
    this.contactmoment = contactmoment;
    return this;
  }

 /**
   * URL-referentie naar de KLANT.
   * @return klant
  **/
  public URI getKlant() {
    return klant;
  }

  /**
    * Set klant
  **/
  public void setKlant(URI klant) {
    this.klant = klant;
  }

  public KlantContactMoment klant(URI klant) {
    this.klant = klant;
    return this;
  }

 /**
   * De rol van de KLANT in het CONTACTMOMENT. Indien de KLANT zowel gesprekspartner als belanghebbende is, dan worden er twee KLANTCONTACTMOMENTen aangemaakt.
   * @return rol
  **/
  public RolEnum getRol() {
    return rol;
  }

  /**
    * Set rol
  **/
  public void setRol(RolEnum rol) {
    this.rol = rol;
  }

  public KlantContactMoment rol(RolEnum rol) {
    this.rol = rol;
    return this;
  }


  /**
    * Create a string representation of this pojo.
  **/
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class KlantContactMoment {\n");

    sb.append("    url: ").append(toIndentedString(url)).append("\n");
    sb.append("    contactmoment: ").append(toIndentedString(contactmoment)).append("\n");
    sb.append("    klant: ").append(toIndentedString(klant)).append("\n");
    sb.append("    rol: ").append(toIndentedString(rol)).append("\n");
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
