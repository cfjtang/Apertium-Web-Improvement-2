package com.panayotis.jubler.tools.translate.plugins;
import com.panayotis.jubler.tools.translate.GenericApertiumTranslator;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import static com.panayotis.jubler.i18n.I18N._;
import java.util.Properties;


/**
 *
 * @author sourcemorph
 */
public class ApertiumWebTranslator extends GenericApertiumTranslator {

    ArrayList<String> sourceLang;
    ArrayList<ArrayList<String>> destLang;

    public ApertiumWebTranslator() {
        /*Properties systemSettings = System.getProperties();
        systemSettings.put("http.proxyHost", "10.1.2.21");
        systemSettings.put("http.proxyPort", "9090");
        System.setProperties(systemSettings);*/
        sourceLang = new ArrayList<String>();
        destLang = new ArrayList<ArrayList<String>>();
        String result = "";
        try {
           String theURL = "http://xixona.dlsi.ua.es/webservice/ws.php?";
           URL url = new URL(theURL);
           URLConnection urlConn = url.openConnection();
           BufferedReader webReader = new BufferedReader(new InputStreamReader(urlConn.getInputStream(),"UTF-8"));
           result = webReader.readLine();
           //System.out.println(result);
           result = webReader.readLine();
           //System.out.println(result);
           int hyphenIndex;
           do {
               result.trim();
               System.out.println(result);
               hyphenIndex = result.indexOf("-");
               if(result.indexOf("-", hyphenIndex+1) == -1) {
                   String t1 = result.substring(hyphenIndex-2, hyphenIndex);
                   String t2 = result.substring(hyphenIndex+1,hyphenIndex+3);
                   //System.out.println(t1+"-"+t2);
                   String fullt1 = findLanguageID(t1);
                   String fullt2 = findLanguageID(t2);
                   //System.out.println(fullt1+"-"+fullt2);
                   if(fullt1 != null && fullt2 != null) {
                       if(!sourceLang.contains(fullt1)) {
                           sourceLang.add(fullt1);
                           destLang.add(new ArrayList<String>());
                       }
                       destLang.get(sourceLang.indexOf(fullt1)).add(fullt2);
                   }
               }
               result = webReader.readLine();
           } while(result != null || result.equals("<br />") == false);
           webReader.close();
       } catch(Exception ex) {
           ex.printStackTrace();
       }
    }

    public String[] getSourceLanguages() {
        String [] temp = new String[sourceLang.size()];
        int count = 0;
        for(String s : sourceLang) {
            temp[count++] = s;
            //System.out.println(s);
        }
        return temp;
    }

    public String[] getDestinationLanguagesFor(String from) {
        int current = sourceLang.indexOf(from);
        String [] temp = new String[destLang.get(current).size()];
        int count = 0;
        //System.out.println("dest:"+from);
        for(String s : destLang.get(current)) {
            temp[count++] = s;
            //System.out.println(s);
        }
        return temp;
    }

    public String getDefaultSourceLanguage() {
        return _(sourceLang.get(0));
    }

    public String getDefaultDestinationLanguage() {
        return _(destLang.get(0).get(0));
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
