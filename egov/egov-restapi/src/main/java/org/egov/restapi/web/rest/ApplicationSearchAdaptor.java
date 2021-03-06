/*
 * eGov suite of products aim to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) <2015>  eGovernments Foundation
 *
 *     The updated version of eGov suite of products as by eGovernments Foundation
 *     is available at http://www.egovernments.org
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see http://www.gnu.org/licenses/ or
 *     http://www.gnu.org/licenses/gpl.html .
 *
 *     In addition to the terms of the GPL license to be adhered to in using this
 *     program, the following additional terms are to be complied with:
 *
 *         1) All versions of this program, verbatim or modified must carry this
 *            Legal Notice.
 *
 *         2) Any misrepresentation of the origin of the material is prohibited. It
 *            is required that all modified versions of this material be marked in
 *            reasonable ways as different from the original version.
 *
 *         3) This license does not grant any rights to any user of the program
 *            with regards to rights under trademark law for use of the trade names
 *            or trademarks of eGovernments Foundation.
 *
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */
package org.egov.restapi.web.rest;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.egov.search.domain.Document;
import org.egov.search.domain.SearchResult;

import java.lang.reflect.Type;
import java.util.HashMap;

public class ApplicationSearchAdaptor implements JsonSerializer<SearchResult> {

    @Override
    public JsonElement serialize(final SearchResult searchResult, final Type type, final JsonSerializationContext jsc) {
        final JsonArray jsonArray = new JsonArray();
        HashMap<String, Object> clausesMap = new HashMap<String, Object>(0);
        HashMap<String, Object> searchableMap = new HashMap<String, Object>(0);
        for (Document document : searchResult.getDocuments()) {
            JsonObject jsonObject = new JsonObject();
            clausesMap = (HashMap) document.getResource().get("clauses");
            jsonObject.addProperty("applicationtype", clausesMap.get("applicationtype").toString());
            jsonObject.addProperty("status", clausesMap.get("status").toString());

            searchableMap = (HashMap) document.getResource().get("searchable");
            jsonObject.addProperty("applicationnumber", searchableMap.get("applicationnumber").toString());
            jsonObject.addProperty("applicationdate", searchableMap.get("applicationdate").toString());
            jsonObject.addProperty("applicantname", searchableMap.get("applicantname").toString());
            jsonObject.addProperty("applicantaddress", searchableMap.get("applicantAddress").toString());
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }

}
