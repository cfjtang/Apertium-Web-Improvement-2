/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.scalemt.router.ws;

import org.scalemt.rmi.transferobjects.Format;
import org.scalemt.rmi.transferobjects.LanguagePair;
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
        String translation=LoadBalancer.getInstance().translate(sourceText, pair, Format.txt, UserType.anonymous, null);
        return translation;
        }
        catch(Exception e)
        {
            return "";
        }
    }

    public byte[] translateDocument(byte[] sourceDocument,String sourceLang, String targetLang,String format )
    {
        //TODO: implement
        return new byte[100];
    }

}
