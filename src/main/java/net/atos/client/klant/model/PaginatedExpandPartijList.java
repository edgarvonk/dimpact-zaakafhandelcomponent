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
import java.util.ArrayList;
import java.util.List;

import jakarta.json.bind.annotation.JsonbProperty;


public class PaginatedExpandPartijList {

    @JsonbProperty("count")
    private Integer count;

    @JsonbProperty("next")
    private URI next;

    @JsonbProperty("previous")
    private URI previous;

    @JsonbProperty("results")
    private List<ExpandPartij> results = new ArrayList<>();

    /**
     * Get count
     * 
     * @return count
     **/
    public Integer getCount() {
        return count;
    }

    /**
     * Set count
     */
    public void setCount(Integer count) {
        this.count = count;
    }

    public PaginatedExpandPartijList count(Integer count) {
        this.count = count;
        return this;
    }

    /**
     * Get next
     * 
     * @return next
     **/
    public URI getNext() {
        return next;
    }

    /**
     * Set next
     */
    public void setNext(URI next) {
        this.next = next;
    }

    public PaginatedExpandPartijList next(URI next) {
        this.next = next;
        return this;
    }

    /**
     * Get previous
     * 
     * @return previous
     **/
    public URI getPrevious() {
        return previous;
    }

    /**
     * Set previous
     */
    public void setPrevious(URI previous) {
        this.previous = previous;
    }

    public PaginatedExpandPartijList previous(URI previous) {
        this.previous = previous;
        return this;
    }

    /**
     * Get results
     * 
     * @return results
     **/
    public List<ExpandPartij> getResults() {
        return results;
    }

    /**
     * Set results
     */
    public void setResults(List<ExpandPartij> results) {
        this.results = results;
    }

    public PaginatedExpandPartijList results(List<ExpandPartij> results) {
        this.results = results;
        return this;
    }

    public PaginatedExpandPartijList addResultsItem(ExpandPartij resultsItem) {
        if (this.results == null) {
            this.results = new ArrayList<>();
        }
        this.results.add(resultsItem);
        return this;
    }


    /**
     * Create a string representation of this pojo.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class PaginatedExpandPartijList {\n");

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
