package com.panayotis.jubler.tools.translate.plugins;
import com.panayotis.jubler.tools.translate.GenericApertiumTranslator;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import static com.panayotis.jubler.i18n.I18N._;
/**
 *
 * @author sourcemorph
 */
public class ApertiumWebTranslator extends GenericApertiumTranslator {

    public String getDefaultSourceLanguage() {
        return _("English");
    }

    public String getDefaultDestinationLanguage() {
        return _("Spanish");
    }

    public String[] getSourceLanguages() {
        String[] langs = new String[lang.size()];
        for (int i = 0; i < lang.size(); i++) {
            langs[i] = lang.get(i).getName();
        }
        return langs;
    }

    public String[] getDestinationLanguagesFor(String from) {
        String[] langs = new String[lang.size()];
        for (int i = 0; i < lang.size(); i++) {
            langs[i] = lang.get(i).getName();
        }
        return langs;
    }

    public String getDefinition() {
        return _("Apertium Web translate");
    }

    public String machineTranslation(String text, String so_lang, String de_lang) {
        String result = "";
       try {
           String text2 = URLEncoder.encode(text,"UTF-8");
           String theURL = "http://xixona.dlsi.ua.es/webservice/ws.php?mark=0&mode=" + so_lang + "-" + de_lang +"&text=" + text2 + "";
           URL url = new URL(theURL);
           URLConnection urlConn = url.openConnection();
           BufferedReader webReader = new BufferedReader(new InputStreamReader(urlConn.getInputStream(),"UTF-8"));
           result = webReader.readLine();
           webReader.close();
       } catch(Exception ex) {
           ex.printStackTrace();
       }
       return result;
    }
}