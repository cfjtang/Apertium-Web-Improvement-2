/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.scalemt.router.ws;

import org.scalemt.rmi.transferobjects.BinaryDocument;
import org.scalemt.rmi.transferobjects.Format;
import org.scalemt.rmi.transferobjects.LanguagePair;
import org.scalemt.rmi.transferobjects.TextContent;
import org.scalemt.router.logic.LoadBalancer;
import org.scalemt.router.logic.UserType;


/**
 *
 * @author vmsanchez
 */
public class TranslateXMLRPC {

    public String translate(String sourceText, String sourceLang, String targetLang)
    {
        try
        {
        LanguagePair pair= new LanguagePair(sourceLang, targetLang);
        String translation=LoadBalancer.getInstance().translate(new TextContent(Format.txt,sourceText), pair, UserType.anonymous, null).toString();
        return translation;
        }
        catch(Exception e)
        {
            return "";
        }
    }

    public byte[] translateDocument(byte[] sourceDocument,String sourceLang, String targetLang,String format )
    {
        try
        {
         LanguagePair pair= new LanguagePair(sourceLang, targetLang);
         byte[] translatedDocuemnt =LoadBalancer.getInstance().translate(new BinaryDocument( Format.rtf,sourceDocument), pair, UserType.anonymous, null).toByteArray();
        return translatedDocuemnt;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return new byte[1];
        }
    }

}
