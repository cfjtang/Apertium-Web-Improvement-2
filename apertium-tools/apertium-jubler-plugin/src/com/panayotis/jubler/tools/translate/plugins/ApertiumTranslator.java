/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.panayotis.jubler.tools.translate.plugins;
import static com.panayotis.jubler.i18n.I18N._;
import com.panayotis.jubler.subs.SubEntry;
import com.panayotis.jubler.tools.translate.Language;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Vector;

/**
 *
 * @author sourcemorph
 */
public class ApertiumTranslator implements com.panayotis.jubler.tools.translate.Translator {

    private static Vector<Language> lang;
    static {
        lang = new Vector<Language>();
        lang = new Vector<Language>();
        lang.add(new Language("ar", _("Arabic")));
        lang.add(new Language("bg", _("Bulgarian")));
        lang.add(new Language("zh-CN", _("Chinese")));
        lang.add(new Language("hr", _("Croatian")));
        lang.add(new Language("cs", _("Czech")));
        lang.add(new Language("da", _("Danish")));
        lang.add(new Language("nl", _("Dutch")));
        lang.add(new Language("en", _("English")));
        lang.add(new Language("fi", _("Finnish")));
        lang.add(new Language("fr", _("French")));
        lang.add(new Language("de", _("German")));
        lang.add(new Language("el", _("Greek")));
        lang.add(new Language("hi", _("Hindi")));
        lang.add(new Language("it", _("Italian")));
        lang.add(new Language("ja", _("Japanese")));
        lang.add(new Language("ko", _("Korean")));
        lang.add(new Language("no", _("Norwegian")));
        lang.add(new Language("pl", _("Polish")));
        lang.add(new Language("pt", _("Portuguese")));
        lang.add(new Language("ro", _("Romanian")));
        lang.add(new Language("ru", _("Russian")));
        lang.add(new Language("es", _("Spanish")));
        lang.add(new Language("sv", _("Swedish")));
    }

    public String getDefaultSourceLanguage() {
        return _("English");
    }

    public String getDefaultDestinationLanguage() {
        return _("Spanish");
    }

    protected Vector<Language> getLanguages() {
        return lang;
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
        return _("Apertium translate");
    }

    public String findLanguage(String language) {
        for (Language l : getLanguages()) {
            if (l.getName().equals(language)) {
                return l.getID();
            }
        }
        return "";
    }

    public boolean translate (Vector<SubEntry> subs, String from_language, String to_language) {
        Iterator<SubEntry> it = subs.iterator();
        SubEntry current;
        String transSent, so_lang, de_lang;
        //System.out.println(from_language);
        //System.out.println(to_language);
        so_lang = findLanguage(from_language);
        de_lang = findLanguage(to_language);
        while(it.hasNext()) {
            current = it.next();
            transSent = current.getText();
            String[] cmd = {
            "/bin/sh",
            "-c",
            "echo \"" + current.getText() + "\" | apertium " + so_lang + "-" + de_lang
            };
            System.out.println(cmd);
            try {
                Process Findspace = Runtime.getRuntime().exec(cmd);
                BufferedReader Resultset = new BufferedReader(new InputStreamReader (Findspace.getInputStream(), "UTF-8"));
                transSent = Resultset.readLine();
                System.out.println(transSent);
            }catch(IOException ex) {
                System.out.println("Could not translate, perhaps Apertium isn't installed correctly");
                ex.printStackTrace();
            }
            current.setText(transSent);
        }
        return true;
    }
}
