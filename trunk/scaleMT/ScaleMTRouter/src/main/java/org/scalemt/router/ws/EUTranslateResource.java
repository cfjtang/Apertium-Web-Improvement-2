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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author vmsanchez
 */
@Path("itranslate")
public class EUTranslateResource {

     static Log logger = LogFactory.getLog(EUTranslateResource.class);

    @GET
    @Produces("application/json")
    @Consumes("application/json")
    public String getJSON(@Context HttpServletRequest request) {
        
        logger.trace("Processing GET request");

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
                    logger.warn("Error creating input JSON object", ex);
                }

                return translate(jo).toString();
            }
            else
            {
                 //TODO: Log, HTTP error code
                logger.warn("Data object not provided");
                return "";
            }
        }
        else
        {
            //TODO: http error code
            return "";
        }
        
    }

    @POST
    //@Consumes({"application/x-www-form-urlencoded", "multipart/form-data"})
    @Produces("application/json")
    @Consumes("application/json")
    public String postJSON(InputStream is,@Context HttpServletRequest request) {

        logger.trace("Processing POST request");

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
                    //TODO: Log, HTTP error code
                    logger.warn("Error creating input JSON object", ex);
                }
                return translate(jo).toString();
            }
            else
            {
                logger.trace("Null POST data");
            }

             return "";
        }
        else
        {
            //TODO: http error code
            return "";
        }

    }

    private JSONObject translate(JSONObject jo)
    {
        /*
       String source=jo.getString("src");
       String target=jo.getString("target");
       JSONArray sgms=jo.getJSONArray("sgms");
       */
        return new JSONObject();
    }
}
