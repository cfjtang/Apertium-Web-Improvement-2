/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.scalemt.router.ws;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.scalemt.rmi.exceptions.TranslationEngineException;
import org.scalemt.rmi.transferobjects.AdditionalTranslationOptions;
import org.scalemt.rmi.transferobjects.Format;
import org.scalemt.rmi.transferobjects.LanguagePair;
import org.scalemt.rmi.transferobjects.TextContent;
import org.scalemt.router.logic.LoadBalancer;
import org.scalemt.router.logic.NoEngineForThatPairException;
import org.scalemt.router.logic.TooMuchLoadException;
import org.scalemt.router.logic.Util;

/**
 *
 * @author vmsanchez
 */
@Path("itranslate")
public class EUTranslateResource {

     static Log logger = LogFactory.getLog(EUTranslateResource.class);

     private  String APIKEY;

    public EUTranslateResource() {
        APIKEY=Util.readConfigurationProperty("itranslate4eu_key");
    }



    @GET
    @Produces("application/json")
    @Consumes("application/json")
    public Response getJSON(@Context HttpServletRequest request) {
        
        logger.trace("Processing GET request");

        String ip=request.getRemoteAddr();
        String apacheProxyHeader=request.getHeader("X-Forwarded-For");
        if(apacheProxyHeader!=null)
        {
            String[] ips=apacheProxyHeader.split(",");
            if(ips.length>0)
                if(ips[0].length()>0)
                    ip=ips[0];
        }

        String function=request.getParameter("func");
        if("translate".equals(function))
        {
            String data=request.getParameter("data");
            if(data!=null)
            {
                JSONObject jo=null;
                try {
                    jo = new JSONObject(data);
                } catch (JSONException ex) {
                    //TODO: Log, HTTP error code
                    logger.trace("Error creating input JSON object", ex);
                }

                int code= translate(jo,ip,request.getHeader("Referer"));
                if(code==200)
                    return  Response.ok(jo.toString()).build();
                else
                {
                    return Response.status(code).build();
                }
            }
            else
            {
                 //TODO: Log, HTTP error code
                logger.warn("Data object not provided");
               return Response.status(400).build();
            }
        }
         else if("info".equals(function))
        {
            return  Response.ok(info().toString()).build();
        }
        else
        {
            //TODO: http error code
           return Response.status(400).build();
        }
        
    }

    @POST
    //@Consumes({"application/x-www-form-urlencoded", "multipart/form-data"})
    @Produces("application/json")
    @Consumes({"application/json","application/x-www-form-urlencoded", "multipart/form-data"})
    public Response postJSON(InputStream is,@Context HttpServletRequest request) {

        logger.trace("Processing POST request");

        String ip=request.getRemoteAddr();
        String apacheProxyHeader=request.getHeader("X-Forwarded-For");
        if(apacheProxyHeader!=null)
        {
            String[] ips=apacheProxyHeader.split(",");
            if(ips.length>0)
                if(ips[0].length()>0)
                    ip=ips[0];
        }

        String function=request.getParameter("func");
        if("translate".equals(function))
        {
            String input=null;
            BufferedReader reader=null;
            try
            {
                reader=new BufferedReader(new InputStreamReader(is));
                StringBuilder builder=new StringBuilder();
                String line;
                while((line=reader.readLine())!=null)
                    builder.append(line).append("\n");

                input=builder.toString();
                System.err.println(input);

            }
            catch(IOException e)
            {
               logger.warn("Error reading POST data", e);
               
            }
            finally
            {
                if(reader!=null)
                    try {
                    reader.close();
                } catch (IOException ex) {
                   logger.warn("Error reading POST data", ex);
                }
            }


            if(input!=null)
            {
                logger.trace("POST data: "+input);
                JSONObject jo=null;
                try {
                    jo = new JSONObject(input);
                } catch (JSONException ex) {
                    
                    logger.warn("Error creating input JSON object from '"+input+"'", ex);
                   return Response.status(400).build();
                    
                }
               
                int code= translate(jo,ip,request.getHeader("Referer"));
                if(code==200)
                    return  Response.ok(jo.toString()).build();
                else
                {
                    return Response.status(code).build();
                }
               
               
            }
            else
            {
                logger.trace("Null POST data");

            }

            return Response.status(400).build();
        }
        else if("info".equals(function))
        {
            return  Response.ok(info().toString()).build();
        }
        else
        {
            //TODO: http error code
            return Response.status(400).build();
        }

    }

    private JSONObject info()
    {
       JSONObject infoObj=new JSONObject();
       try
       {
       infoObj.put("version",Util.readConfigurationProperty("itranslate4eu_version"));
       infoObj.put("vendor",Util.readConfigurationProperty("itranslate4eu_vendor"));
       infoObj.put("engine",Util.readConfigurationProperty("itranslate4eu_engine"));
       infoObj.put("logo",Util.readConfigurationProperty("itranslate4eu_logo"));
       JSONArray lp=new JSONArray();
       for(LanguagePair lpair: LoadBalancer.getInstance().getSupportedPairs())
       {
           JSONArray pair=new JSONArray();
           if(!lpair.getSource().contains("_") && !lpair.getTarget().contains("_"))
           {
               pair.put(lpair.getSource());
               pair.put(lpair.getTarget());
               lp.put(pair);
           }
           
       }
       infoObj.put("lp",lp);
       infoObj.put("features", new JSONObject());

        }
       catch(JSONException jse)
       {
           logger.warn("Error creating info object", jse);
       }
       return infoObj;
    }

    private int translate(JSONObject jo, String ip, String referer)
    {

        int responseCode=200;
     try
       {
       String source=jo.getString("src");
       String target=jo.getString("trg");
       JSONArray sgms=jo.getJSONArray("sgms");
       String tid=jo.getString("tid");
       jo.remove("tid");
       JSONArray newsgms=new JSONArray();
      
           for(int i=0; i<sgms.length(); i++)
           {
               JSONObject newSegmElem= new JSONObject();
               JSONArray newUnits= new JSONArray();

               JSONObject segm_elem=sgms.getJSONObject(i);
               JSONArray units=segm_elem.getJSONArray("units");
               for(int j=0; j<units.length(); j++)
               {
                   JSONObject newUnitElem= new JSONObject();

                   JSONObject unit_elem=units.getJSONObject(j);
                   String text=unit_elem.getString("text");
                   AdditionalTranslationOptions ato= new AdditionalTranslationOptions();
                   ato.getOptions().put("markUnknown", "no");
                   String translation = LoadBalancer.getInstance().translate(new TextContent(Format.txt,text), new LanguagePair(source, target),ip, referer ,APIKEY,ato).toString();

                   if(translation.length()>0)
                       translation=translation.substring(0, translation.length()-1);
                   newUnitElem.put("text", translation);
                   newUnits.put(newUnitElem);
               }

               newSegmElem.put("units", newUnits);
               newsgms.put(newSegmElem);
           }
       jo.remove("sgms");
       jo.put("sgms", newsgms);
        }
     catch(JSONException jse)
     {
         responseCode=400;
     }
       catch (NoEngineForThatPairException nepe) {
                responseCode = 503;
            } catch (TooMuchLoadException tmle) {
                responseCode = 500;
            } catch (TranslationEngineException e) {
                responseCode = 500;
            } catch (Exception ex) {
                ex.printStackTrace();
                responseCode = 500;
            }

       return responseCode;
    }
}
