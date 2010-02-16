/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.scalemt.router.ws;

import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlrpc.XmlRpcException;
import org.scalemt.rmi.exceptions.TranslationEngineException;
import org.scalemt.rmi.transferobjects.AdditionalTranslationOptions;
import org.scalemt.rmi.transferobjects.BinaryDocument;
import org.scalemt.rmi.transferobjects.Format;
import org.scalemt.rmi.transferobjects.LanguagePair;
import org.scalemt.rmi.transferobjects.TextContent;
import org.scalemt.router.logic.LoadBalancer;
import org.scalemt.router.logic.NoEngineForThatPairException;
import org.scalemt.router.logic.TooMuchLoadException;


/**
 *
 * @author vmsanchez
 */
public class TranslateXMLRPC {

    /**
     * Commons-logging logger
     */
    static Log logger = LogFactory.getLog(TranslateXMLRPC.class);

    public String translate(String sourceText,String format, String sourceLang, String targetLang,String key) throws XmlRpcException
    {
        String ip=MyXmlRpcServlet.clientIpAddress.get().toString();
        int code=200;
        String errorMessage="";
        String translation=null;
       
        LoggerStatiticsWriter.getInstance().logRequestReceived(ip, key, sourceLang+"|"+targetLang, format);
        logger.debug("requestreceived "+ip+" "+key+" "+sourceLang+"|"+targetLang);
        Format f=null;
        LanguagePair p = null;
        try
        {
        f=Format.valueOf(format);
        }
        catch(IllegalArgumentException iae)
        {
            code=452;
            errorMessage = "Unsupported format";
        }

        try
        {
        p=new LanguagePair(sourceLang, targetLang);
        }
        catch(IllegalArgumentException iae)
        {
            code=400;
            errorMessage = "Bad language pair format";
        }

        if (code == 200) {
            try {
                List<LanguagePair> supportedPairs = LoadBalancer.getInstance().getSupportedPairs();
                if (supportedPairs.contains(p)) {

                    AdditionalTranslationOptions additionalTranslationOptions=new AdditionalTranslationOptions();
                    translation = LoadBalancer.getInstance().translate(new TextContent(f,sourceText), p,ip ,key ,additionalTranslationOptions).toString();
                } else {
                    errorMessage = "Not supported pair";
                    code = 451;
                }

            } catch (IllegalArgumentException iae) {
                errorMessage = "Unsupported format";
                code = 452;
            } catch (NoEngineForThatPairException nepe) {
                errorMessage = "No translation engines available";
                code = 551;
            } catch (TooMuchLoadException tmle) {
                errorMessage = "Your translations limit has been reached";
                code = 552;
            } catch (TranslationEngineException e) {
                errorMessage = "Unexpected Error";
                code = 500;
            } catch (Exception ex) {
                ex.printStackTrace();
                errorMessage = "Unexpected Error";
                code = 500;
            }
        }

        LoggerStatiticsWriter.getInstance().logRequestProcessed(Integer.toString(code));
        logger.debug("requesprocessed "+code+" "+errorMessage);

        if(code==200)
            return translation;
        else
            throw new XmlRpcException(code, errorMessage);    
    }

    public byte[] translateDocument(byte[] sourceDocument,String format,String sourceLang, String targetLang, String key ) throws XmlRpcException
    {
          String ip=MyXmlRpcServlet.clientIpAddress.get().toString();

        int code=200;
        String errorMessage="";
       byte[] translation=null;

        LoggerStatiticsWriter.getInstance().logRequestReceived(ip, key, sourceLang+"|"+targetLang, format);
        logger.debug("requestreceived "+ip+" "+key+" "+sourceLang+"|"+targetLang);
        Format f=null;
        LanguagePair p = null;
        try
        {
        f=Format.valueOf(format);
        }
        catch(IllegalArgumentException iae)
        {
            code=452;
            errorMessage = "Unsupported format";
        }

        try
        {
        p=new LanguagePair(sourceLang, targetLang);
        }
        catch(IllegalArgumentException iae)
        {
            code=400;
            errorMessage = "Bad language pair format";
        }

        if (code == 200) {
            try {
                List<LanguagePair> supportedPairs = LoadBalancer.getInstance().getSupportedPairs();
                if (supportedPairs.contains(p)) {

                    AdditionalTranslationOptions additionalTranslationOptions=new AdditionalTranslationOptions();
                    translation = LoadBalancer.getInstance().translate(new BinaryDocument(f,sourceDocument), p,ip ,key ,additionalTranslationOptions).toByteArray();
                } else {
                    errorMessage = "Not supported pair";
                    code = 451;
                }

            } catch (IllegalArgumentException iae) {
                errorMessage = "Unsupported format";
                code = 452;
            } catch (NoEngineForThatPairException nepe) {
                errorMessage = "No translation engines available";
                code = 551;
            } catch (TooMuchLoadException tmle) {
                errorMessage = "Your translations limit has been reached";
                code = 552;
            } catch (TranslationEngineException e) {
                errorMessage = "Unexpected Error";
                code = 500;
            } catch (Exception ex) {
                ex.printStackTrace();
                errorMessage = "Unexpected Error";
                code = 500;
            }
        }

        LoggerStatiticsWriter.getInstance().logRequestProcessed(Integer.toString(code));
        logger.debug("requesprocessed "+code+" "+errorMessage);

        if(code==200)
            return translation;
        else
            throw new XmlRpcException(code, errorMessage);
    }
}
