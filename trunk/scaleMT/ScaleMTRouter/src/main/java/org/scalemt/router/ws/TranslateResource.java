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

import java.io.StringWriter;
import org.scalemt.rmi.exceptions.TranslationEngineException;
import org.scalemt.rmi.transferobjects.Format;
import org.scalemt.rmi.transferobjects.LanguagePair;
import org.scalemt.router.logic.LoadBalancer;
import org.scalemt.router.logic.Util;

import org.scalemt.router.logic.NoEngineForThatPairException;
import org.scalemt.router.logic.TooMuchLoadException;


import org.scalemt.router.logic.UserType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
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
import org.scalemt.rmi.exceptions.DaemonDeadException;
import org.scalemt.rmi.exceptions.NonZeroExitValueException;
import org.scalemt.rmi.exceptions.NotAvailableDaemonException;
import org.scalemt.rmi.exceptions.RouterTimeoutException;
import org.scalemt.rmi.exceptions.SlaveTimeoutException;
import org.scalemt.rmi.transferobjects.AdditionalTranslationOptions;
import org.scalemt.rmi.transferobjects.TextContent;
import org.scalemt.router.logic.TooLongSourceException;
import org.scalemt.router.logic.TooManyUserRequestsException;

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
    public String getJSON(@Context HttpServletRequest request) {

        /*
         *  Log client IP address contained in X-Forwarded-For header,
         * if request passed through a proxy
         */
        String ip=request.getRemoteAddr();
        String apacheProxyHeader=request.getHeader("X-Forwarded-For");
        if(apacheProxyHeader!=null)
        {
            String[] ips=apacheProxyHeader.split(",");
            if(ips.length>0)
                if(ips[0].length()>0)
                    ip=ips[0];
        }


        return process(uricontext.getQueryParameters(),ip,request.getHeader("Referer"));
    }

    /**
     * Process a translation request via HTTP POST
     * @param params POST parameters
     * @return JSON data with the translation and result code
     */
    @POST
    @Consumes({"application/x-www-form-urlencoded", "multipart/form-data"})
    @Produces("application/json")
    public String postJSON(MultivaluedMap<String, String> params,@Context HttpServletRequest request) {

        /*
         *  Log client IP address contained in X-Forwarded-For header,
         * if request passed through a proxy
         */
        String ip=request.getRemoteAddr();
        String apacheProxyHeader=request.getHeader("X-Forwarded-For");
        if(apacheProxyHeader!=null)
        {
            String[] ips=apacheProxyHeader.split(",");
            if(ips.length>0)
                if(ips[0].length()>0)
                    ip=ips[0];
        }

        return process(params,ip,request.getHeader("Referer"));
    }

    /**
     * This method does the real work. It's invoked from GET and POST frontends
     *
     * @param params HTTP parameters
     * @return JSON data with the translation and result code
     */
    private String process(MultivaluedMap<String, String> params,String clientIp,String referer) {
        //Get parameters
        List<String> q = params.get("q");
        List<String> langpairs = params.get("langpair");
        String callback = params.getFirst("callback");
        String context = params.getFirst("context");
        String format = params.getFirst("format");
        String key = params.getFirst("key");

        Map<String,String> additionalOptions = new HashMap<String, String>();
        for(String mapkey: params.keySet())
        {
            if(!"q".equals(mapkey) && !"langpair".equals(mapkey) && !"callback".equals(mapkey) && !"context".equals(mapkey) && !"format".equals(mapkey) && !"key".equals(mapkey) )
            {
                additionalOptions.put(mapkey, params.getFirst(mapkey));
            }
        }

        JSONObject response = new JSONObject();
        String responseStr;
        String responseDataStr = Constants.JSON_DEFAULT_RESPONSE_DATA;

        //Default response: Bad parameters
        int responseStatus = 400;
        String errorDetails = "Bad parameters";

        String originalTranslation="";
        String dummyTranslation="omegatdummytranslation";
        
        try {
            if (q != null && langpairs != null) {                
                if (q.size() == 1 && langpairs.size() == 1) {
                    /*
                     * If there is only a source text and a language pair, perform one translation
                     */
                    response = getTranslationJSON(q.get(0), langpairs.get(0), format,clientIp,referer, key,additionalOptions);
                    
                    responseStatus = response.getInt(Constants.JSON_RESPONSESTATUS);
                    errorDetails = response.isNull(Constants.JSON_RESPONSEDETAILS) ? null : response.getString(Constants.JSON_RESPONSEDETAILS);
                    
                    //ugly workaround to unescape translation
                    if("omegat".equals(format) && responseStatus==200)
                    {
                       originalTranslation=response.getJSONObject(Constants.JSON_RESPONSEDATA).getString(Constants.JSON_TRANSLATEDTEXT);
                       if(originalTranslation.length()>0)
                       {
                           //originalTranslation=originalTranslation.substring(0, originalTranslation.length()-1)+" "+originalTranslation.substring(originalTranslation.length()-1);
                           originalTranslation=originalTranslation+" \n";
                       }
                       response.getJSONObject(Constants.JSON_RESPONSEDATA).put(Constants.JSON_TRANSLATEDTEXT, dummyTranslation);
                       responseDataStr=response.getJSONObject(Constants.JSON_RESPONSEDATA).toString().replace(dummyTranslation, originalTranslation);
                    }
                    else
                        responseDataStr=response.getJSONObject(Constants.JSON_RESPONSEDATA).toString();
                    
                } else if (q != null && q.size() == 1 && langpairs.size() > 1) {
                    /*
                     * If there is one source text and more than one language pair,
                     * translate the text with the different language pairs.
                     */
                    responseStatus = 200;
                    errorDetails = null;
                    for (String languagePair : langpairs) {
                        JSONObject pairResponse = getTranslationJSON(q.get(0), languagePair, format,clientIp,referer, key,additionalOptions);
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
                        JSONObject pairResponse = getTranslationJSON(query, langpairs.get(0), format, clientIp,referer,key,additionalOptions);
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
                        JSONObject pairResponse = getTranslationJSON(q.get(i), langpairs.get(i), format,clientIp,referer, key,additionalOptions);
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
            //ugly workaround to unescape translation
            if("omegat".equals(format) && responseStatus==200)
            {
                responseStr = response.toString().replace(dummyTranslation, originalTranslation);
            }
            else
            {
                responseStr = response.toString();
            }
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
    private JSONObject getTranslationJSON(String source, String pair, String format,String clientIp,String referer, String key,Map<String,String> moreOptions) throws JSONException {

        String translation = null;
        String errorMessage = null;
        int responseCode = 200;
        Format enumFormat = Format.txt;
                    if (format != null && !"".equals(format)) {
                        enumFormat = Format.valueOf(format);
                    }
         LoggerStatiticsWriter.getInstance().logRequestReceived(clientIp,referer, key, pair, enumFormat.toString());
         logger.debug("requestreceived "+clientIp+" "+referer+" "+key+" "+pair+" "+enumFormat.toString());
        LanguagePair lpair = null;
        try {
            lpair = new LanguagePair(pair, "\\|".toCharArray());
        } catch (IllegalArgumentException iae) {
            errorMessage = "Bad language pair format";
            responseCode = 400;
        }
        if (responseCode == 200) {
            try {
                
                LanguagePair supPair= LoadBalancer.getInstance().convertPairSupported(lpair);
                if (supPair != null) {
                   lpair=supPair;
                    //UserType userType = UserType.anonymous;
                   // if (key != null && UserManagement.getInstance().isKeyValid(key)) {
                   //     userType = UserType.registered;
                    //}
                    AdditionalTranslationOptions additionalTranslationOptions=new AdditionalTranslationOptions();
                    additionalTranslationOptions.getOptions().putAll(moreOptions);
                    translation = LoadBalancer.getInstance().translate(new TextContent(enumFormat,source), lpair,clientIp, referer ,key ,additionalTranslationOptions).toString();
                } else {
                    errorMessage = "Not supported pair";
                    responseCode = 451;
                }

            } catch (IllegalArgumentException iae) {
                errorMessage = "Unsupported format";
                responseCode = 452;
            } catch (NoEngineForThatPairException nepe) {
                errorMessage = "Bad language pair/format";
                responseCode = 453;
            } catch (TooLongSourceException tlse){
                errorMessage = "Source text is too long";
                responseCode = 454;    
            }catch (TooMuchLoadException tmle) {
                errorMessage = "System is overloaded. Please try again in a few minutes.";
                responseCode = 553;
            
           } catch (TooManyUserRequestsException tmure) {
                errorMessage = "Your translations limit has been reached";
                responseCode = 552;
            } 
            catch(DaemonDeadException dde)
            {
                errorMessage = "Daemon dead unexpectedly";
                responseCode = 501;
            }
            catch(SlaveTimeoutException ste)
            {
                errorMessage = "Timeout waiting for translation in slave";
                responseCode = 502;
            }
            catch(RouterTimeoutException rte)
            {
                errorMessage = "Timeout waiting for translation in router";
                responseCode = 503;
            }
            catch(NonZeroExitValueException nzee)
            {
                errorMessage = "Non-zero exit value";
                responseCode = 504;
            }
            catch (NotAvailableDaemonException nade)
            {
                errorMessage = "Not available daemon";
                responseCode = 505;
            }
            catch (TranslationEngineException e) {
                errorMessage = e.getMessage();
                responseCode = 500;
            } catch (Exception ex) {
                ex.printStackTrace();
                errorMessage = "Unexpected Error: "+ex.getMessage();
                responseCode = 500;
            }
        }

        LoggerStatiticsWriter.getInstance().logRequestProcessed(Integer.toString(responseCode));
        logger.debug("requesprocessed "+responseCode+" "+errorMessage);
        
        Map<String, String> responseData = new HashMap<String, String>();
        responseData.put(Constants.JSON_TRANSLATEDTEXT, translation);
        
        return Util.generateJSON(responseData, responseCode, errorMessage);       
    }
}
