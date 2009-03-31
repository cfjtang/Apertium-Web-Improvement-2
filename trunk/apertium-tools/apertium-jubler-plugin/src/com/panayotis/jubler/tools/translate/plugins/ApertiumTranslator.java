package com.panayotis.jubler.tools.translate.plugins;
import com.panayotis.jubler.tools.translate.GenericApertiumTranslator;
import com.panayotis.jubler.tools.translate.Language;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Vector;
import static com.panayotis.jubler.i18n.I18N._;

/**
 *
 * @author sourcemorph
 */
public class ApertiumTranslator extends GenericApertiumTranslator {
    private Vector<Language> sourceLang, destLang;

    public ApertiumTranslator() {
        sourceLang = new Vector<Language>();
        destLang = new Vector<Language>();
        try {
            Process Findspace = Runtime.getRuntime().exec("apertium xxx");
            BufferedReader Resultset = new BufferedReader(new InputStreamReader(Findspace.getInputStream()));
            String current = Resultset.readLine();
            //System.out.println(current);
            current = Resultset.readLine();
            do {
                //System.out.println(current);
                current = current.substring(3);
                String t1,t2;
                t1 = current.substring(0,2);
                t2 = current.substring(3);
                String fullt1 = findLanguageID(t1);
                String fullt2 = findLanguageID(t2);
                sourceLang.add(new Language(t1, _(fullt1)));
                destLang.add(new Language(t2, _(fullt2)));
                //System.out.print(t1+"--"+fullt1);
                //System.out.println(t2+"--"+fullt2);
                current = Resultset.readLine();
            } while(current!=null);
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }

    public String getDefinition() {
        return _("Apertium translate");
    }

    public String getDefaultSourceLanguage() {
        System.out.println("default s.language");
        return _(sourceLang.get(0).getName());
    }

    public String getDefaultDestinationLanguage() {
        System.out.println("default d.language");
        return _(destLang.get(0).getName());
    }

    public String[] getSourceLanguages() {
        System.out.println("Source Lang=");
        String[] langs = new String[sourceLang.size()];
        for (int i = 0; i < sourceLang.size(); i++) {
            langs[i] = sourceLang.get(i).getName();
            System.out.println(langs[i]);
        }
        return langs;
    }

    public String[] getDestinationLanguagesFor(String from) {
        System.out.println("Dest Lang=");
        Vector<String> temp = new Vector<String>();
        String [] langs;
        for(int i=0; i<sourceLang.size(); i++) {
            Language l = sourceLang.get(i);
            if(l.getName().equals(from)) {
                temp.add(destLang.get(i).getName());
            }
        }
        langs = new String[temp.size()];
        int count = 0;
        for(String s : temp) {
            langs[count++] = s;
            System.out.println(langs[count]);
        }
        return langs;
    }

    public String machineTranslation (String text, String so_lang, String de_lang) {
        String result = "";
        String[] cmd = {"apertium",  so_lang + "-" + de_lang};
            try {
                Process Findspace = Runtime.getRuntime().exec(cmd);
                OutputStream output = Findspace.getOutputStream();
                output.write(text.getBytes());
                output.close();
                BufferedReader Resultset = new BufferedReader(new InputStreamReader (Findspace.getInputStream(), "UTF-8"));
                result = Resultset.readLine();
                Resultset.close();
                Findspace.destroy();
            } catch(IOException ex) {
                System.out.println("Could not translate, perhaps Apertium isn't installed correctly");
                ex.printStackTrace();
            }
        return result;
    }
}
