/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.scalemt.router.ws;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scalemt.rmi.transferobjects.BinaryDocument;
import org.scalemt.rmi.transferobjects.Format;
import org.scalemt.rmi.transferobjects.LanguagePair;
import org.scalemt.rmi.transferobjects.TextContent;
import org.scalemt.router.logic.LoadBalancer;


/**
 *
 * @author vmsanchez
 */
public class TranslateXMLRPC {

    /**
     * Commons-logging logger
     */
    static Log logger = LogFactory.getLog(TranslateXMLRPC.class);

    public String translate(String sourceText, String sourceLang, String targetLang,String key)
    {
        String ip=MyXmlRpcServlet.clientIpAddress.get().toString();
        try
        {
        logger.debug("requestreceived "+ip+" "+key+" "+sourceLang+"|"+targetLang);
        LanguagePair pair= new LanguagePair(sourceLang, targetLang);
        String translation=LoadBalancer.getInstance().translate(new TextContent(Format.txt,sourceText), pair,ip ,key, null).toString();
        return translation;
        }
        catch(Exception e)
        {
            return "";
        }
        finally
        {
            logger.debug("requesprocessed");
        }
    }

    public byte[] translateDocument(byte[] sourceDocument,String sourceLang, String targetLang,String format, String key )
    {
          String ip=MyXmlRpcServlet.clientIpAddress.get().toString();
        try
        {
            logger.debug("requestreceived "+ip+" "+key+" "+sourceLang+"|"+targetLang);
         LanguagePair pair= new LanguagePair(sourceLang, targetLang);
         byte[] translatedDocuemnt =LoadBalancer.getInstance().translate(new BinaryDocument( Format.rtf,sourceDocument),pair, ip,key, null).toByteArray();
        return translatedDocuemnt;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return new byte[1];
        }
        finally{
            logger.debug("requesprocessed");
        }
    }

}
