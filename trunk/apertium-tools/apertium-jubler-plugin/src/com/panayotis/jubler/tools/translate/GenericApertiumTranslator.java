package com.panayotis.jubler.tools.translate;
import com.panayotis.jubler.subs.SubEntry;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;
import static com.panayotis.jubler.i18n.I18N._;

/**
 *
 * @author sourcemorph
 */
public abstract class GenericApertiumTranslator implements com.panayotis.jubler.tools.translate.Translator {

    protected static Vector<Language> lang;
    ArrayList<String> sourceLang;
    ArrayList<ArrayList<String>> destLang;

    static {
        lang = new Vector<Language>();
        lang.add(new Language("ca", _("Catalan")));
        lang.add(new Language("en", _("English")));
        lang.add(new Language("eo", _("Esparanto")));
        lang.add(new Language("es", _("Spanish")));
        lang.add(new Language("oc", _("Occitan")));
        lang.add(new Language("gl", _("Galician")));
        lang.add(new Language("ro", _("Romanian")));
        lang.add(new Language("pt", _("Portuguese")));
        lang.add(new Language("fr", _("French")));
        lang.add(new Language("cy", _("Welsh")));
        lang.add(new Language("eu", _("Basque")));
        lang.add(new Language("hi", _("Hindi")));
    }

    public GenericApertiumTranslator(BufferedReader r) {
        sourceLang = new ArrayList<String>();
        destLang = new ArrayList<ArrayList<String>>();
        String result = "";
        try {
            result = r.readLine();
            //System.out.println(result);
            result = r.readLine();
            //System.out.println(result);
            int hyphenIndex;
            do {
                result.trim();
                hyphenIndex = result.indexOf("-");
                if(result.indexOf("-", hyphenIndex+1) == -1) {
                    if(result.contains("<br/>")) {
                        result = result.substring(0,result.length()-5);
                        //System.out.println(result);
                    }
                    String t1 = result.substring(hyphenIndex-2, hyphenIndex);
                    String t2 = result.substring(hyphenIndex+1);
                    //System.out.println(t1+"-"+t2);
                    if(t2.length() == 2) {
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
                }
                result = r.readLine();
            } while(result != null && result.equals("<br />") == false);
        } catch(IOException ex) {
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

    public String findLanguage(String language) {
        for (Language l : getLanguages()) {
            if (l.getName().equals(language)) {
                return l.getID();
            }
        }
        return "";
    }

    public String findLanguageID(String langID) {
        for (Language l : getLanguages()) {
            if (l.getID().equals(langID)) {
                return l.getName();
            }
        }
        return null;
    }

    protected Vector<Language> getLanguages() {
        return lang;
    }

    public boolean translate (Vector<SubEntry> subs, String from_language, String to_language) {
        Iterator<SubEntry> it = subs.iterator();
        SubEntry current;
        String origSent, transSent, so_lang, de_lang;
        //System.out.println(from_language);
        //System.out.println(to_language);
        so_lang = findLanguage(from_language);
        de_lang = findLanguage(to_language);
        while(it.hasNext()) {
            current = it.next();
            origSent = current.getText();
            transSent = machineTranslation(origSent, so_lang, de_lang);
            if(transSent.length() == 0) {
                transSent = origSent;
                System.out.println("Apertium did not translate");
            }
            current.setText(machineTranslation(transSent, so_lang, de_lang));
        }
        return true;
    }

    public abstract String machineTranslation(String text, String so_lang, String de_lang);
}