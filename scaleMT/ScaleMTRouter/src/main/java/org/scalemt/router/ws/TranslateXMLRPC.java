/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.scalemt.router.ws;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlrpc.XmlRpcException;
import org.scalemt.rmi.exceptions.DaemonDeadException;
import org.scalemt.rmi.exceptions.NonZeroExitValueException;
import org.scalemt.rmi.exceptions.NotAvailableDaemonException;
import org.scalemt.rmi.exceptions.RouterTimeoutException;
import org.scalemt.rmi.exceptions.SlaveTimeoutException;
import org.scalemt.rmi.exceptions.TranslationEngineException;
import org.scalemt.rmi.transferobjects.AdditionalTranslationOptions;
import org.scalemt.rmi.transferobjects.BinaryDocument;
import org.scalemt.rmi.transferobjects.Format;
import org.scalemt.rmi.transferobjects.LanguagePair;
import org.scalemt.rmi.transferobjects.TextContent;
import org.scalemt.router.logic.LoadBalancer;
import org.scalemt.router.logic.NoEngineForThatPairException;
import org.scalemt.router.logic.TooLongSourceException;
import org.scalemt.router.logic.TooManyUserRequestsException;
import org.scalemt.router.logic.TooMuchLoadException;


/**
 * XML-RPC endpoint. All the public methods of this class are exposed through the XML-RPC API
 *
 * @author vmsanchez
 */
public class TranslateXMLRPC {

    /**
     * Commons-logging logger
     */
    static Log logger = LogFactory.getLog(TranslateXMLRPC.class);


    public String translate(String sourceText,String format, String sourceLang, String targetLang,boolean markUnkown,String key) throws XmlRpcException
    {
        return translateP(sourceText, format, sourceLang, targetLang, markUnkown, key);
    }

    public String translate(String sourceText,String format, String sourceLang, String targetLang,String key) throws XmlRpcException
    {
        return translateP(sourceText, format, sourceLang, targetLang, false, key);
    }

    /**
     * Translates text content.
     *
     * @param sourceText Text to be translated
     * @param format Format of the text: "txt" for plain text, "html" for html
     * @param sourceLang Source language code
     * @param targetLang Target language code
     * @param markUnkown Put an asterisk next to each unknown word
     * @param key USer API Key
     * @return Translation
     * @throws XmlRpcException
     */
    private String translateP(String sourceText,String format, String sourceLang, String targetLang,boolean markUnkown,String key) throws XmlRpcException
    {
        String ip=MyXmlRpcServlet.clientIpAddress.get().toString();
        int code=200;
        String errorMessage="";
        String translation=null;
        
        LoggerStatiticsWriter.getInstance().logRequestReceived(ip,"xml-rpc", key, sourceLang+"|"+targetLang, format);
        logger.debug("requestreceived "+ip+" xml-rpc "+key+" "+sourceLang+"|"+targetLang);
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
                 LanguagePair supPair= LoadBalancer.getInstance().convertPairSupported(p);
                if (supPair != null) {
                   p=supPair;

                    AdditionalTranslationOptions additionalTranslationOptions=new AdditionalTranslationOptions();
                    if(markUnkown)
                    {
                        additionalTranslationOptions.getOptions().put("markUnknown", "yes");
                    }
                    else
                    {
                        additionalTranslationOptions.getOptions().put("markUnknown", "no");
                    }
                    translation = LoadBalancer.getInstance().translate(new TextContent(f,sourceText), p,ip,"xml-rpc",key ,additionalTranslationOptions).toString();
                } else {
                    errorMessage = "Not supported pair";
                    code = 451;
                }

            } catch (IllegalArgumentException iae) {
                errorMessage = "Unsupported format";
                code = 452;
            } catch (NoEngineForThatPairException nepe) {
                errorMessage = "Bad language pair/format";
                code = 453;
            } catch (TooLongSourceException tlse){
                errorMessage = "Source text is too long";
                code = 454;    
            }catch (TooMuchLoadException tmle) {
                errorMessage = "System is overloaded. Please try again in a few minutes.";
                code = 553;
            
           } catch (TooManyUserRequestsException tmure) {
                errorMessage = "Your translations limit has been reached";
                code = 552;
            } 
            catch(DaemonDeadException dde)
            {
                errorMessage = "Daemon dead unexpectedly";
                code = 501;
            }
            catch(SlaveTimeoutException ste)
            {
                errorMessage = "Timeout waiting for translation in slave";
                code = 502;
            }
            catch(RouterTimeoutException rte)
            {
                errorMessage = "Timeout waiting for translation in router";
                code = 503;
            }
            catch(NonZeroExitValueException nzee)
            {
                errorMessage = "Non-zero exit value";
                code = 504;
            
            }
             catch (NotAvailableDaemonException nade)
            {
                errorMessage = "Not avaiable daemon";
                code = 505;
            }
            catch (TranslationEngineException e) {
                errorMessage = e.getMessage();
                code = 500;
            } catch (Exception ex) {
                ex.printStackTrace();
                errorMessage = "Unexpected Error: "+ex.getMessage();
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

    public byte[] translateDocument(byte[] sourceDocument,String format,String sourceLang, String targetLang, boolean markUnknown,String key ) throws XmlRpcException
    {
        return translateDocumentP(sourceDocument, format, sourceLang, targetLang, markUnknown, key);
    }
    
    public byte[] translateDocument(byte[] sourceDocument,String format,String sourceLang, String targetLang, String key ) throws XmlRpcException
    {
        return translateDocumentP(sourceDocument, format, sourceLang, targetLang, false, key);
    }

    /**
     * Translates a document, received as a binary file
     *
     * @param sourceDocument Document to be translated
     * @param format "rtf" for RTF, "odt" for ODT. See Format and BinaryDocument
     * @param sourceLang Source language code
     * @param targetLang Target language code
     * @param markUnknown Put an asterisk next to each unknown word
     * @param key key USer API Key
     * @return Translated document
     * @throws XmlRpcException
     */
    private byte[] translateDocumentP(byte[] sourceDocument,String format,String sourceLang, String targetLang, boolean markUnknown,String key ) throws XmlRpcException
    {
        String ip=MyXmlRpcServlet.clientIpAddress.get().toString();

        int code=200;
        String errorMessage="";
        byte[] translation=null;

        LoggerStatiticsWriter.getInstance().logRequestReceived(ip,"xml-rpc", key, sourceLang+"|"+targetLang, format);
        logger.debug("requestreceived "+ip+" xml-rpc "+key+" "+sourceLang+"|"+targetLang);
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
                LanguagePair supPair= LoadBalancer.getInstance().convertPairSupported(p);
                if (supPair != null) {
                   p=supPair;

                    AdditionalTranslationOptions additionalTranslationOptions=new AdditionalTranslationOptions();
                    if(markUnknown)
                    {
                        additionalTranslationOptions.getOptions().put("markUnknown", "yes");
                    }
                    else
                    {
                        additionalTranslationOptions.getOptions().put("markUnknown", "no");
                    }
                    translation = LoadBalancer.getInstance().translate(new BinaryDocument(f,sourceDocument), p,ip ,"xml-rpc",key ,additionalTranslationOptions).toByteArray();
                } else {
                    errorMessage = "Not supported pair";
                    code = 451;
                }

            } catch (IllegalArgumentException iae) {
                errorMessage = "Unsupported format";
                code = 452;
            } catch (NoEngineForThatPairException nepe) {
                errorMessage = "Bad language pair/format";
                code = 453;
            } catch (TooLongSourceException tlse){
                errorMessage = "Source text is too long";
                code = 454;    
            } catch (TooMuchLoadException tmle) {
                errorMessage = "System is overloaded. Please try again in a few minutes.";
                code = 553;
            
           } catch (TooManyUserRequestsException tmure) {
                errorMessage = "Your translations limit has been reached";
                code = 552;
            } 
            catch(DaemonDeadException dde)
            {
                errorMessage = "Daemon dead unexpectedly";
                code = 501;
            }
            catch(SlaveTimeoutException ste)
            {
                errorMessage = "Timeout waiting for translation in slave";
                code = 502;
            }
            catch(RouterTimeoutException rte)
            {
                errorMessage = "Timeout waiting for translation in router";
                code = 503;
            }
            catch(NonZeroExitValueException nzee)
            {
                errorMessage = "Non-zero exit value";
                code = 504;
            
            }
             catch (NotAvailableDaemonException nade)
            {
                errorMessage = "Not avaiable daemon";
                code = 505;
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

    public List<Map<String,String>> getSupportedLanguagePairs()
    {
        List<LanguagePair> pairs=LoadBalancer.getInstance().getSupportedPairs();
        List<Map<String,String>> returnList = new ArrayList<Map<String, String>>(pairs.size());
        for(LanguagePair p: pairs)
        {
            Map<String,String> mapPair =new HashMap<String, String>();
            mapPair.put("sourceLanguage", p.getSource());
            mapPair.put("targetLanguage", p.getTarget());
            returnList.add(mapPair);
        }

        return returnList;
    }
}
