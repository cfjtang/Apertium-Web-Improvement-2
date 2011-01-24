/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.scalemt.router.ws;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author vmsanchez
 */
@Path("itranslate")
public class EUTranslateResource {

    @GET
    @Produces("application/json")
    public String getJSON(@Context HttpServletRequest request) {
        
        String function=request.getParameter("func");
        if("translate".equals(function))
        {
            JSONObject jo=null;
            try {
                jo = new JSONObject(request.getParameter("data"));
            } catch (JSONException ex) {
                //TODO: Log, HTTP error code
                Logger.getLogger(EUTranslateResource.class.getName()).log(Level.SEVERE, null, ex);
            }
            return translate(jo).toString();
        }
        else
        {
            //TODO: http error code
            return "";
        }
        
    }

    @POST
    @Produces("application/json")
    public String postJSON(@Context HttpServletRequest request) {

        String function=request.getParameter("func");
        if("translate".equals(function))
        {
            //request.getReader();

            JSONObject jo=null;
            try {
                jo = new JSONObject(request.getParameter("data"));
            } catch (JSONException ex) {
                //TODO: Log, HTTP error code
                Logger.getLogger(EUTranslateResource.class.getName()).log(Level.SEVERE, null, ex);
            }
            return translate(jo).toString();
        }
        else
        {
            //TODO: http error code
            return "";
        }

    }

    private JSONObject translate(JSONObject jo)
    {
        //TODO: all
        return new JSONObject();
    }
}
