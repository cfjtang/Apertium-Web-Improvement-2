/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

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
    private Vector<Language> sourceLang, destLang, sourceLangNoDup;

    public void checkPairs() {
        sourceLang = new Vector<Language>();
        destLang = new Vector<Language>();
        sourceLangNoDup = new Vector<Language>();
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
                boolean flag = false;
                for(Language l : sourceLangNoDup) {
                    if(l.getID().equals(t1)) {
                        flag = true;
                        break;
                    }
                }

               if(!flag) {
                   sourceLangNoDup.add(new Language(t1, _(fullt1)));
               }
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
        checkPairs();
        System.out.println("default s.language");
        return _(sourceLang.get(0).getName());
    }

    public String getDefaultDestinationLanguage() {
        checkPairs();
        System.out.println("default d.language");
        return _(destLang.get(0).getName());
    }

    public String[] getSourceLanguages() {
        checkPairs();
        System.out.println("Source Lang=");
        String[] langs = new String[sourceLangNoDup.size()];
        for (int i = 0; i < sourceLangNoDup.size(); i++) {
            langs[i] = sourceLangNoDup.get(i).getName();
            //System.out.println(langs[i]);
        }
        return langs;
    }

    public String[] getDestinationLanguagesFor(String from) {
        checkPairs();
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
            //System.out.println(langs[count]);
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

    /*public static void main(String [] args) {
        ApertiumTranslator n = new ApertiumTranslator();
        String [] n1 = n.getSourceLanguages();
        for(int i=0;i<n1.length;i++)
            System.out.println(n1[i]);
        n1 = n.getDestinationLanguagesFor("English");
        for(int i=0;i<n1.length;i++)
            System.out.println(n1[i]);
    }*/
}
