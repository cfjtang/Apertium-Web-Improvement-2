/*
 *  ApertiumServer. Highly scalable web service implementation for Apertium.
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
package com.gsoc.apertium.translationengines.router.ws;

import com.gsoc.apertium.translationengines.rmi.exceptions.TranslationEngineException;
import com.gsoc.apertium.translationengines.rmi.transferobjects.Format;
import com.gsoc.apertium.translationengines.rmi.transferobjects.LanguagePair;
import com.gsoc.apertium.translationengines.router.logic.LoadBalancer;
import com.gsoc.apertium.translationengines.router.logic.Util;
import com.gsoc.apertium.translationengines.router.ws.Constants;

import com.gsoc.apertium.translationengines.router.logic.NoEngineForThatPairException;
import com.gsoc.apertium.translationengines.router.logic.TooMuchLoadException;


import com.gsoc.apertium.translationengines.router.logic.UserManagement;
import com.gsoc.apertium.translationengines.router.logic.UserType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MultivaluedMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Translation REST Web Service
 *
 * @author vitaka
 */
@Path("translate")
public class TranslateResource {

    @Context
    private UriInfo uricontext;

     /**
     * Commons-logging logger
     */
    static Log logger = LogFactory.getLog(TranslateResource.class);

    /** Creates a new instance of TranslateResource */
    public TranslateResource() {
    }

    /**
     * Process a translation request via HTTP GET
     * @return JSON data with the translation and result code
     */
    @GET
    @Produces("application/json")
    public String getJSON() {
        return process(uricontext.getQueryParameters());
    }

    /**
     * Process a translation request via HTTP POST
     * @param params POST parameters
     * @return JSON data with the translation and result code
     */
    @POST
    @Consumes({"application/x-www-form-urlencoded", "multipart/form-data"})
    @Produces("application/json")
    public String postJSON(MultivaluedMap<String, String> params) {
        return process(params);
    }

    /**
     * This method does the real work. It's invoked from GET and POST frontends
     *
     * @param params HTTP parameters
     * @return JSON data with the translation and result code
     */
    private String process(MultivaluedMap<String, String> params) {
        //Get parameters
        List<String> q = params.get("q");
        List<String> langpairs = params.get("langpair");
        String callback = params.getFirst("callback");
        String context = params.getFirst("context");
        String format = params.getFirst("format");
        String key = params.getFirst("key");

        JSONObject response = new JSONObject();
        String responseStr;
        String responseDataStr = Constants.JSON_DEFAULT_RESPONSE_DATA;

        //Default response: Bad parameters
        int responseStatus = 400;
        String errorDetails = "Bad parameters";

        try {
            if (q != null && langpairs != null) {
                if (q.size() == 1 && langpairs.size() == 1) {
                    /*
                     * If there is only a source text and a language pair, perform one translation
                     */
                    response = getTranslationJSON(q.get(0), langpairs.get(0), format, key);
                    responseDataStr=response.getJSONObject(Constants.JSON_RESPONSEDATA).toString();
                    responseStatus = response.getInt(Constants.JSON_RESPONSESTATUS);
                    errorDetails = response.isNull(Constants.JSON_RESPONSEDETAILS) ? null : response.getString(Constants.JSON_RESPONSEDETAILS);

                } else if (q != null && q.size() == 1 && langpairs.size() > 1) {
                    /*
                     * If there is one source text and more than one language pair,
                     * translate the text with the different language pairs.
                     */
                    responseStatus = 200;
                    errorDetails = null;
                    for (String languagePair : langpairs) {
                        JSONObject pairResponse = getTranslationJSON(q.get(0), languagePair, format, key);
                        response.accumulate(Constants.JSON_RESPONSEDATA, pairResponse);
                        if (pairResponse.getInt(Constants.JSON_RESPONSESTATUS) != 200) {
                            responseStatus = pairResponse.getInt(Constants.JSON_RESPONSESTATUS);
                            errorDetails = pairResponse.getString(Constants.JSON_RESPONSEDETAILS);
                        }
                    }
                    responseDataStr=response.getJSONArray(Constants.JSON_RESPONSEDATA).toString();
                } else if (langpairs.size() == 1 && q.size() > 1) {
                    /*
                     * If there is one language pair and more than one source text,
                     * translate the texts with the same language pair.
                     */
                    responseStatus = 200;
                    errorDetails = null;
                    for (String query : q) {
                        JSONObject pairResponse = getTranslationJSON(query, langpairs.get(0), format, key);
                        response.accumulate(Constants.JSON_RESPONSEDATA, pairResponse);
                        if (pairResponse.getInt(Constants.JSON_RESPONSESTATUS) != 200) {
                            responseStatus = pairResponse.getInt(Constants.JSON_RESPONSESTATUS);
                            errorDetails = pairResponse.getString(Constants.JSON_RESPONSEDETAILS);
                        }
                    }
                    responseDataStr=response.getJSONArray(Constants.JSON_RESPONSEDATA).toString();
                } else if (langpairs.size() == q.size() && q.size() > 1) {
                    /*
                     * If there are the same number of source texts and language pair,
                     * translate each text with the language pair that is in the same position
                     * in query parameters.
                     */
                    responseStatus = 200;
                    errorDetails = null;
                    for (int i = 0; i < q.size(); i++) {
                        JSONObject pairResponse = getTranslationJSON(q.get(i), langpairs.get(i), format, key);
                        response.accumulate(Constants.JSON_RESPONSEDATA, pairResponse);
                        if (pairResponse.getInt(Constants.JSON_RESPONSESTATUS) != 200) {
                            responseStatus = pairResponse.getInt(Constants.JSON_RESPONSESTATUS);
                            errorDetails = pairResponse.getString(Constants.JSON_RESPONSEDETAILS);
                        }
                    }
                    responseDataStr=response.getJSONArray(Constants.JSON_RESPONSEDATA).toString();
                } else {
                    //Error: bad parameters
                    response.put(Constants.JSON_RESPONSEDATA, JSONObject.NULL);
                    responseDataStr=response.getJSONObject(Constants.JSON_RESPONSEDATA).toString();
                }
            } else {
                //Error: bad parameters
                response.put(Constants.JSON_RESPONSEDATA, JSONObject.NULL);
                responseDataStr=response.getJSONObject(Constants.JSON_RESPONSEDATA).toString();
            }

            //Put response status and error details in JSON response
            response.put(Constants.JSON_RESPONSESTATUS, responseStatus);
            response.put(Constants.JSON_RESPONSEDETAILS, errorDetails == null ? JSONObject.NULL : errorDetails);


        } catch (JSONException e) {
            e.printStackTrace();
            response = null;
        }

        if (response == null) {
            //If there have been an exception building JSON, create error response manually
            responseStr = Constants.JSON_DEFAULT_RESPONSE;
            responseDataStr=Constants.JSON_DEFAULT_RESPONSE_DATA;
        } else {
            responseStr = response.toString();
        }

        //Process callback and context parameters
        return Util.formatJSONOutput(responseStr,responseDataStr, callback, context, responseStatus, errorDetails);

    }

    /**
     * Process a translation of a single piece of text with a single language pair
     *
     * @param source Source text
     * @param pair language pair
     * @return JSON object with translation and status codes
     * @throws org.json.JSONException
     */
    private JSONObject getTranslationJSON(String source, String pair, String format, String key) throws JSONException {

        String translation = null;
        String errorMessage = null;
        int responseCode = 200;

        logger.debug("requestreceived "+key+" "+pair);
        LanguagePair lpair = null;
        try {
            lpair = new LanguagePair(pair, "\\|".toCharArray());
        } catch (IllegalArgumentException iae) {

            errorMessage = "Bad language pair format";
            responseCode = 400;
        }
        if (responseCode == 200) {
            try {
                List<LanguagePair> supportedPairs = LoadBalancer.getInstance().getSupportedPairs();
                if (supportedPairs.contains(lpair)) {
                    Format enumFormat = Format.text;
                    if (format != null && !"".equals(format)) {
                        enumFormat = Format.valueOf(format);
                    }
                    UserType userType = UserType.anonymous;
                    if (key != null && UserManagement.getInstance().isKeyValid(key)) {
                        userType = UserType.registered;
                    }
                    translation = LoadBalancer.getInstance().translate(source, lpair, enumFormat, userType,null);
                } else {
                    errorMessage = "Not supported pair";
                    responseCode = 451;
                }

            } catch (IllegalArgumentException iae) {

                errorMessage = "Unsupported format";
                responseCode = 452;
            } catch (NoEngineForThatPairException nepe) {
                errorMessage = "No translation engines available";
                responseCode = 551;
            } catch (TooMuchLoadException tmle) {
                errorMessage = "System is overloaded";
                responseCode = 552;
            } catch (TranslationEngineException e) {
                errorMessage = e.getMessage();
                responseCode = 500;
            } catch (Exception ex) {
                ex.printStackTrace();
                errorMessage = "Unexpected Error";
                responseCode = 500;
            }
        }

        logger.debug("requesprocessed "+responseCode+" "+errorMessage);

        Map<String, String> responseData = new HashMap<String, String>();
        responseData.put(Constants.JSON_TRANSLATEDTEXT, translation);
        return Util.generateJSON(responseData, responseCode, errorMessage);
    }
}
