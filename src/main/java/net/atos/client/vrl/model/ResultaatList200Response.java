/**
 * Referentielijsten & Selectielijst API
 *  Een API om referentielijstwaarden en de gemeentelijke selectielijst te benaderen.  ## Selectielijst  De [Gemeentelijke Selectielijst](https://vng.nl/selectielijst) is relevant in het kader van archivering.  **Zaakgericht werken**  Bij het configureren van zaaktypes (en resultaattypes) in de catalogus API refereren een aantal resources naar resources binnen de Selectielijst API. Het gaat dan om de `ProcesType` en `Resultaat` resources.  ## Referentielijsten  Referentielijsten bevat een standaardset aan waarden. Deze waarden zijn net té dynamisch om in een enum opgenomen te worden, maar er is wel behoefte om deze landelijk te standaardiseren. Een voorbeeld hiervan is de set aan mogelijke communicatiekanalen.  ## Autorisatie  Deze APIs zijn alleen-lezen, en behoeven geen autorisatie.  ## Inhoud  De inhoud wordt beheerd door VNG Realisatie. Om de inhoud van referentielijsten bij te werken, contacteer dan VNG Realisatie via e-mail of op Github.  De inhoud van de Gemeentelijke Selectielijst wordt geïmporteerd vanuit de gepubliceerde Excel-bestanden.
 *
 * The version of the OpenAPI document: 1.0.0-alpha
 * Contact: standaarden.ondersteuning@vng.nl
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */

package net.atos.client.vrl.model;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import jakarta.json.bind.annotation.JsonbProperty;


public class ResultaatList200Response  {

  @JsonbProperty("count")
  private Integer count;

  @JsonbProperty("next")
  private URI next;

  @JsonbProperty("previous")
  private URI previous;

  @JsonbProperty("results")
  private List<Resultaat> results = new ArrayList<>();

 /**
   * Get count
   * @return count
  **/
  public Integer getCount() {
    return count;
  }

  /**
    * Set count
  **/
  public void setCount(Integer count) {
    this.count = count;
  }

  public ResultaatList200Response count(Integer count) {
    this.count = count;
    return this;
  }

 /**
   * Get next
   * @return next
  **/
  public URI getNext() {
    return next;
  }

  /**
    * Set next
  **/
  public void setNext(URI next) {
    this.next = next;
  }

  public ResultaatList200Response next(URI next) {
    this.next = next;
    return this;
  }

 /**
   * Get previous
   * @return previous
  **/
  public URI getPrevious() {
    return previous;
  }

  /**
    * Set previous
  **/
  public void setPrevious(URI previous) {
    this.previous = previous;
  }

  public ResultaatList200Response previous(URI previous) {
    this.previous = previous;
    return this;
  }

 /**
   * Get results
   * @return results
  **/
  public List<Resultaat> getResults() {
    return results;
  }

  /**
    * Set results
  **/
  public void setResults(List<Resultaat> results) {
    this.results = results;
  }

  public ResultaatList200Response results(List<Resultaat> results) {
    this.results = results;
    return this;
  }

  public ResultaatList200Response addResultsItem(Resultaat resultsItem) {
    this.results.add(resultsItem);
    return this;
  }


  /**
    * Create a string representation of this pojo.
  **/
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ResultaatList200Response {\n");

    sb.append("    count: ").append(toIndentedString(count)).append("\n");
    sb.append("    next: ").append(toIndentedString(next)).append("\n");
    sb.append("    previous: ").append(toIndentedString(previous)).append("\n");
    sb.append("    results: ").append(toIndentedString(results)).append("\n");
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
