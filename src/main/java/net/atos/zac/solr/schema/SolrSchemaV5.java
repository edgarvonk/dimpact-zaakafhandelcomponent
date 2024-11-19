/*
 * SPDX-FileCopyrightText: 2023 Atos
 * SPDX-License-Identifier: EUPL-1.2+
 */

package net.atos.zac.solr.schema;

import static net.atos.zac.solr.FieldType.STRING;
import static net.atos.zac.solr.SolrSchemaUpdateHelper.addFieldMultiValued;

import java.util.List;
import java.util.Set;

import org.apache.solr.client.solrj.request.schema.SchemaRequest;

import net.atos.zac.solr.SolrSchemaUpdate;
import net.atos.zac.zoeken.model.zoekobject.ZoekObjectType;

class SolrSchemaV5 implements SolrSchemaUpdate {

    @Override
    public int getVersie() {
        return 5;
    }

    @Override
    public Set<ZoekObjectType> getTeHerindexerenZoekObjectTypes() {
        return Set.of(ZoekObjectType.ZAAK);
    }

    @Override
    public List<SchemaRequest.Update> getSchemaUpdates() {
        return List.of(addFieldMultiValued("zaak_bagObjecten", STRING, true, true));
    }
}
