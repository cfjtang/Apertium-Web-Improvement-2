/*
 *  ScaleMT. Highly scalable framework for machine translation web services
 *  Copyright (C) 2009  Víctor Manuel Sánchez Cartagena
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.scalemt.router.ws;

import org.scalemt.rmi.transferobjects.LanguagePair;
import org.scalemt.router.logic.LoadBalancer;
import org.scalemt.router.ws.Constants;
import org.scalemt.router.logic.Util;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MultivaluedMap;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Listing of supported language pairs REST Web Service
 *
 * @author vitaka
 */
@Path("listPairs")
public class ListPairsResource {
    @Context
    private UriInfo uricontext;

    /** Creates a new instance of ListPairsResource */
    public ListPairsResource() {

    }

    /**
     * Processes a list pairs request vua HTTP GET
     * @return JSON data with the supported language pairs and result code
     */
    @GET
    @Produces("application/json")
    public String getJSON() {

        return process(uricontext.getQueryParameters());
    }

    /**
     * * Processes a list pairs request vua HTTP POST
     * @param params POST parameters
     * @return JSON data with the supported language pairs and result code
     */
    @POST
    @Consumes("application/x-www-form-urlencoded")
    @Produces("application/json")
    public String postJSON(MultivaluedMap<String, String> params)
    {
        return process(params);
    }

    /**
     * This method does the real work. It'ss invoked from GET and POST frontends
     *
     * @param params HTTP parameters
     * @return JSON data with the suported language pairs and result code
     */
    private String process(MultivaluedMap<String, String> params) {
        String callback = params.getFirst("callback");
        String context = params.getFirst("context");
        
        int responseCode=200;
        String responseDetails=null;

        List<LanguagePair> supportedPairs = LoadBalancer.getInstance().getSupportedPairs();

        List<Map<String,String>> data = new LinkedList<Map<String, String>>();
        for(LanguagePair pair:supportedPairs)
        {
            Map<String,String> pairdata = new HashMap<String, String>();
            pairdata.put(Constants.JSON_SOURCELANG,pair.getSource());
            pairdata.put(Constants.JSON_TARGETLANG,pair.getTarget());
            data.add(pairdata);
        }

        String responseStr=null;
        String responseDataStr=null;
        try
        {
            JSONObject jSONObject= Util.generateJSONArray(data, responseCode, responseDetails);
            responseStr= jSONObject.toString();
            responseDataStr=jSONObject.getJSONArray(Constants.JSON_RESPONSEDATA).toString();
        }
        catch(JSONException e)
        {
            responseStr=Constants.JSON_DEFAULT_RESPONSE;
            responseDataStr=Constants.JSON_DEFAULT_RESPONSE_DATA;
        }
        return Util.formatJSONOutput(responseStr,responseDataStr, callback, context, responseCode, responseDetails);

          
    }

    
}
